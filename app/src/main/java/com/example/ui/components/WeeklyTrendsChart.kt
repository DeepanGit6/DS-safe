package com.example.ui.components

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeeklyAnalyticsSummary
import com.example.data.model.WeeklyDayStat
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanContainer
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.SafetyGreen
import com.example.ui.theme.SafetyGreenContainer
import java.util.Locale

@Composable
fun WeeklyTrendsCard(
    summary: WeeklyAnalyticsSummary?,
    onExportPdf: (Context, Boolean) -> Unit,
    onViewAllTrends: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedDayIndex by remember { mutableStateOf(0) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analytics",
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Weekly Safety Trends",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Safe Transit Telemetry Audit",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // 100% Nominal Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SafetyGreenContainer.copy(alpha = 0.35f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SafetyGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "100% NOMINAL",
                        color = SafetyGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Metrics Summary Grid
            if (summary != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricChip(
                        title = "Trips",
                        value = "${summary.totalSessions}",
                        color = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MetricChip(
                        title = "Duration",
                        value = "${summary.totalMinutes}m",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MetricChip(
                        title = "Distance",
                        value = "${String.format(Locale.US, "%.1f", summary.totalDistanceKm)}k",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MetricChip(
                        title = "Streak",
                        value = "${summary.safeStreakDays}d",
                        color = SafetyGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive 7-Day Chart Canvas
                WeeklyBarChartCanvas(
                    dailyStats = summary.dailyStats,
                    selectedIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Selected Day Details Card
                val activeDayStat = summary.dailyStats.getOrNull(selectedDayIndex) ?: summary.dailyStats.first()
                SelectedDayDetailStrip(stat = activeDayStat)

                Spacer(modifier = Modifier.height(14.dp))

                // Export PDF Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Export & Share PDF Button
                    Button(
                        onClick = { onExportPdf(context, true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_pdf_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share PDF",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share PDF",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Print / View PDF Button
                    OutlinedButton(
                        onClick = { onExportPdf(context, false) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("export_pdf_print_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print PDF",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Print / View",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.7f))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WeeklyBarChartCanvas(
    dailyStats: List<WeeklyDayStat>,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxMinutes = (dailyStats.maxOfOrNull { it.totalMinutes } ?: 60).coerceAtLeast(60).toFloat()
    val barColorActive = CyberCyanContainer
    val barColorInactive = ElectricBlue.copy(alpha = 0.35f)
    val baselineColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(dailyStats) {
                    detectTapGestures { offset ->
                        val barSlotWidth = size.width / dailyStats.size
                        val tappedIndex = (offset.x / barSlotWidth).toInt().coerceIn(0, dailyStats.size - 1)
                        onSelectDay(tappedIndex)
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val slotWidth = canvasWidth / dailyStats.size
            val barWidth = slotWidth * 0.48f

            // Baseline guide line
            drawLine(
                color = baselineColor,
                start = Offset(0f, canvasHeight - 20f),
                end = Offset(canvasWidth, canvasHeight - 20f),
                strokeWidth = 1.dp.toPx()
            )

            dailyStats.forEachIndexed { index, stat ->
                val x = index * slotWidth + (slotWidth - barWidth) / 2
                val barHeight = ((stat.totalMinutes / maxMinutes) * (canvasHeight - 40f)).coerceAtLeast(8f)
                val y = (canvasHeight - 20f) - barHeight

                val isSelected = index == selectedIndex

                // Bar fill
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = if (isSelected) {
                            listOf(CyberCyan, ElectricBlue)
                        } else {
                            listOf(barColorInactive, barColorInactive.copy(alpha = 0.15f))
                        }
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Active glowing cap for selected bar
                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = 2.5.dp.toPx(),
                        center = Offset(x + barWidth / 2, y + 4.dp.toPx())
                    )
                }
            }
        }

        // Day Labels Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dailyStats.forEachIndexed { index, stat ->
                val isSelected = index == selectedIndex
                Text(
                    text = stat.day,
                    color = if (isSelected) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { onSelectDay(index) }
                )
            }
        }
    }
}

@Composable
private fun SelectedDayDetailStrip(stat: WeeklyDayStat) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.6f))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = SafetyGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${stat.fullDayName}: ${stat.sessionCount} Safe Trips (${stat.totalMinutes} mins)",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "${String.format(Locale.US, "%.1f", stat.distanceKm)} km covered",
                color = CyberCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
