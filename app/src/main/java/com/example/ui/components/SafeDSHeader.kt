package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.SafetyGreen

@Composable
fun SafeDSHeader(
    isDarkMode: Boolean,
    isSyncing: Boolean,
    onToggleDarkMode: () -> Unit,
    onTriggerSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val syncRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "syncRotation"
    )

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Vector Shield Logo Icon
                SafeDSLogoBadge(modifier = Modifier.size(36.dp))

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Safe DS",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.2).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Aegis",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Reach Safe. Stay Safe.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Status Actions: GPS Ready, Sync Trigger, Dark Mode Toggle, Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Real-time Cloud Sync Pill / Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable { onTriggerSync() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("sync_trigger_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Cloud Telemetry",
                            tint = if (isSyncing) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(14.dp)
                                .then(if (isSyncing) Modifier.rotate(syncRotation) else Modifier)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSyncing) "Syncing" else "Live",
                            color = if (isSyncing) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // GPS Ready Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SafetyGreen.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "GPS Ready",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Dark Mode Toggle Icon Button
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .testTag("dark_mode_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = if (isDarkMode) Color(0xFFFFB300) else ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // User Profile Avatar (Ananya)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .testTag("user_profile_avatar")
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuATTGcupIZcZL1MTEB3gzP09AErRrYt5boGktKB2y3xB0zvpNZlgtBPKGhkdmZzSd6ca-0OFyhlJi4kxePjNiI64wu0sbE6YRsC2jLUS5Ynj561LZvWa-Pj4S06ZaD78yFlJsYrqFAckR3vHSc7eabwf-Dmej5RSYrqe3HqIVWIULEsFKjFdxmgReOW80q3QZrH80rpCc_UUG_AmStliPY6dkzx3S4g4CwG_6nPKdhMwh465kYx8EMf",
                        contentDescription = "Profile Ananya",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun SafeDSLogoBadge(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Shield path
        val path = Path().apply {
            moveTo(width * 0.5f, height * 0.05f)
            cubicTo(width * 0.75f, height * 0.08f, width * 0.95f, height * 0.15f, width * 0.95f, height * 0.45f)
            cubicTo(width * 0.95f, height * 0.75f, width * 0.75f, height * 0.92f, width * 0.5f, height * 0.98f)
            cubicTo(width * 0.25f, height * 0.92f, width * 0.05f, height * 0.75f, width * 0.05f, height * 0.45f)
            cubicTo(width * 0.05f, height * 0.15f, width * 0.25f, height * 0.08f, width * 0.5f, height * 0.05f)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(ElectricBlue, Color(0xFF060D24))
            )
        )

        drawPath(
            path = path,
            color = CyberCyan,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
        )

        // Beacon Cone lines
        drawLine(
            color = CyberCyan,
            start = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.55f),
            end = androidx.compose.ui.geometry.Offset(width * 0.35f, height * 0.85f),
            strokeWidth = 2f
        )
        drawLine(
            color = CyberCyan,
            start = androidx.compose.ui.geometry.Offset(width * 0.5f, height * 0.55f),
            end = androidx.compose.ui.geometry.Offset(width * 0.65f, height * 0.85f),
            strokeWidth = 2f
        )
        drawLine(
            color = ElectricBlue,
            start = androidx.compose.ui.geometry.Offset(width * 0.3f, height * 0.85f),
            end = androidx.compose.ui.geometry.Offset(width * 0.7f, height * 0.85f),
            strokeWidth = 2f
        )
    }
}
