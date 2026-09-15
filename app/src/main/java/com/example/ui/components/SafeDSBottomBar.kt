package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SafeDSTab
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmergencyRed

@Composable
fun SafeDSBottomBar(
    currentTab: SafeDSTab,
    onTabSelected: (SafeDSTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.96f),
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(68.dp)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "Home",
                icon = Icons.Default.SpaceDashboard,
                isSelected = currentTab == SafeDSTab.HOME,
                onClick = { onTabSelected(SafeDSTab.HOME) },
                testTag = "tab_home"
            )
            BottomNavItem(
                label = "Journey",
                icon = Icons.Default.NearMe,
                isSelected = currentTab == SafeDSTab.JOURNEY,
                onClick = { onTabSelected(SafeDSTab.JOURNEY) },
                testTag = "tab_journey"
            )
            BottomNavItem(
                label = "SOS",
                icon = Icons.Default.CrisisAlert,
                isSelected = currentTab == SafeDSTab.SOS,
                onClick = { onTabSelected(SafeDSTab.SOS) },
                isSos = true,
                testTag = "tab_sos"
            )
            BottomNavItem(
                label = "Safe Places",
                icon = Icons.Default.Security,
                isSelected = currentTab == SafeDSTab.SAFE_PLACES,
                onClick = { onTabSelected(SafeDSTab.SAFE_PLACES) },
                testTag = "tab_safe_places"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isSos: Boolean = false,
    testTag: String
) {
    val activeColor = when {
        isSos -> EmergencyRed
        else -> CyberCyan
    }

    val unselectedColor = when {
        isSos -> EmergencyRed.copy(alpha = 0.8f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) activeColor else unselectedColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                color = if (isSelected) activeColor else unselectedColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
