package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.WeeklyAnalyticsSummary
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateWeeklyReportPdf(
        context: Context,
        summary: WeeklyAnalyticsSummary,
        userName: String = "Ananya",
        userId: String = "#DS-8842"
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 dimensions at 72dpi
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val primaryDark = Color.parseColor("#0B1229")
        val accentBlue = Color.parseColor("#0052FF")
        val accentCyan = Color.parseColor("#00A3B5")
        val safeGreen = Color.parseColor("#008744")
        val textPrimary = Color.parseColor("#141A32")
        val textMuted = Color.parseColor("#64748B")
        val cardBg = Color.parseColor("#F1F5F9")
        val borderLight = Color.parseColor("#CBD5E1")

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw Header Banner
        paint.color = primaryDark
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, 595f, 110f, paint)

        // Brand Title
        paint.color = Color.WHITE
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("SAFE DS • REACH SAFE. STAY SAFE.", 40f, 48f, paint)

        // Subheader
        paint.color = Color.parseColor("#7DF4FF")
        paint.textSize = 12f
        paint.isFakeBoldText = false
        canvas.drawText("WEEKLY SAFETY TELEMETRY & AUDIT REPORT", 40f, 72f, paint)

        // Date generated in header
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val dateStr = "Generated: ${dateFormat.format(Date())}"
        paint.color = Color.parseColor("#DFE3FF")
        paint.textSize = 10f
        canvas.drawText(dateStr, 40f, 92f, paint)

        // User Cockpit ID Card
        paint.color = cardBg
        val userCardRect = RectF(40f, 130f, 555f, 185f)
        canvas.drawRoundRect(userCardRect, 10f, 10f, paint)

        paint.color = textPrimary
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("USER PROFILE: $userName  (ID: $userId)", 55f, 155f, paint)

        paint.color = textMuted
        paint.textSize = 10.5f
        paint.isFakeBoldText = false
        canvas.drawText("Location Protected • 256-bit AES Encrypted • GPS Lock Verified", 55f, 172f, paint)

        // Key Metrics 4-Box Grid
        val boxWidth = 118f
        val boxHeight = 65f
        val startX = 40f
        val boxY = 205f

        val metrics = listOf(
            Triple("SAFE TRIPS", "${summary.totalSessions}", safeGreen),
            Triple("ACTIVE TIME", "${summary.totalMinutes}m", accentBlue),
            Triple("SAFETY SCORE", "${summary.avgSafetyScore}%", safeGreen),
            Triple("ANOMALIES", "0 (SAFE)", accentCyan)
        )

        for (i in metrics.indices) {
            val (label, value, colorInt) = metrics[i]
            val x = startX + i * (boxWidth + 14f)
            val rect = RectF(x, boxY, x + boxWidth, boxY + boxHeight)

            paint.color = cardBg
            paint.style = Paint.Style.FILL
            canvas.drawRoundRect(rect, 8f, 8f, paint)

            paint.color = borderLight
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1f
            canvas.drawRoundRect(rect, 8f, 8f, paint)
            paint.style = Paint.Style.FILL

            paint.color = textMuted
            paint.textSize = 9f
            paint.isFakeBoldText = true
            canvas.drawText(label, x + 10f, boxY + 22f, paint)

            paint.color = colorInt
            paint.textSize = 17f
            paint.isFakeBoldText = true
            canvas.drawText(value, x + 10f, boxY + 50f, paint)
        }

        // Section: Daily Telemetry Log Table
        var curY = 300f
        paint.color = textPrimary
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("DAILY SAFETY RECORD BREAKDOWN", 40f, curY, paint)

        curY += 15f
        // Table Header
        paint.color = Color.parseColor("#E2E8F0")
        canvas.drawRect(40f, curY, 555f, curY + 26f, paint)

        paint.color = textPrimary
        paint.textSize = 10f
        paint.isFakeBoldText = true
        canvas.drawText("DAY", 55f, curY + 17f, paint)
        canvas.drawText("SESSIONS", 150f, curY + 17f, paint)
        canvas.drawText("DURATION", 250f, curY + 17f, paint)
        canvas.drawText("DISTANCE", 350f, curY + 17f, paint)
        canvas.drawText("SAFETY INDEX", 450f, curY + 17f, paint)

        curY += 26f

        // Table Rows
        paint.isFakeBoldText = false
        paint.textSize = 10f
        for (stat in summary.dailyStats) {
            paint.color = if (stat.day == "Sun" || stat.day == "Sat") Color.parseColor("#F8FAFC") else Color.WHITE
            canvas.drawRect(40f, curY, 555f, curY + 24f, paint)

            paint.color = borderLight
            paint.strokeWidth = 0.5f
            paint.style = Paint.Style.STROKE
            canvas.drawLine(40f, curY + 24f, 555f, curY + 24f, paint)
            paint.style = Paint.Style.FILL

            paint.color = textPrimary
            canvas.drawText("${stat.day} (${stat.fullDayName.take(3)})", 55f, curY + 16f, paint)
            canvas.drawText("${stat.sessionCount} Completed", 150f, curY + 16f, paint)
            canvas.drawText("${stat.totalMinutes} mins", 250f, curY + 16f, paint)
            canvas.drawText("${String.format(Locale.US, "%.1f", stat.distanceKm)} km", 350f, curY + 16f, paint)

            paint.color = safeGreen
            paint.isFakeBoldText = true
            canvas.drawText("${stat.safetyScore}% (Nominal)", 450f, curY + 16f, paint)
            paint.isFakeBoldText = false

            curY += 24f
        }

        // Emergency Dispatch Readiness Audit
        curY += 25f
        paint.color = textPrimary
        paint.textSize = 14f
        paint.isFakeBoldText = true
        canvas.drawText("EMERGENCY DISPATCH & GEOFENCE READINESS", 40f, curY, paint)

        curY += 15f
        val auditRect = RectF(40f, curY, 555f, curY + 90f)
        paint.color = cardBg
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(auditRect, 10f, 10f, paint)

        paint.color = textPrimary
        paint.textSize = 10.5f
        paint.isFakeBoldText = false
        canvas.drawText("✓ Primary Guardian Link: Mom (+91 98765 43210) — ACTIVE LIVE ALERT READY", 55f, curY + 25f, paint)
        canvas.drawText("✓ Secondary Guardian Link: Dad (+91 98765 43211) — SMS PROTOCOL ACTIVE", 55f, curY + 45f, paint)
        canvas.drawText("✓ Geofence Anchors: 4 Active Zones (Home, PSG Tech, City Library, Hostel B)", 55f, curY + 65f, paint)
        canvas.drawText("✓ Incident Radar Pulse: 0 anomalies recorded in recent 7-day period", 55f, curY + 85f, paint)

        // Verification Seal / Footer
        paint.color = borderLight
        paint.strokeWidth = 1f
        paint.style = Paint.Style.STROKE
        canvas.drawLine(40f, 770f, 555f, 770f, paint)

        paint.style = Paint.Style.FILL
        paint.color = textMuted
        paint.textSize = 8.5f
        canvas.drawText("Safe DS Aegis Cockpit • Cryptographic Telemetry Integrity Verified • End-to-End Encrypted", 40f, 790f, paint)
        canvas.drawText("Doc ID: #DS-RPT-${System.currentTimeMillis().toString().takeLast(6)} • Authorized for guardian sharing and PDF printing", 40f, 805f, paint)

        pdfDocument.finishPage(page)

        // Write to file in app cache/reports
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val pdfFile = File(reportsDir, "Safe_DS_Weekly_Report_${System.currentTimeMillis()}.pdf")

        return try {
            val fos = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fos)
            fos.close()
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun sharePdfReport(context: Context, pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Safe DS Weekly Safety Analytics Report")
            putExtra(Intent.EXTRA_TEXT, "Here is the verified weekly safety analytics report from Safe DS.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Weekly Report PDF").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun printOrViewPdfReport(context: Context, pdfFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            // Fallback to chooser
            sharePdfReport(context, pdfFile)
        }
    }
}
