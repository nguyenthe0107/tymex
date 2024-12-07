package com.example.tymexproject.ui.user_detail_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a statistic item with a count, label, and icon in a circular container.
 *
 * @param count The numerical value or count to be displayed (as String)
 * @param label The descriptive text shown below the count
 * @param icon The ImageVector to be displayed as an icon above the count
 *
 *
 * Example usage:
 * ```
 * StatItem(
 *     count = "42",
 *     label = "Followers",
 *     icon = Icons.Default.Person
 * )
 * ```
 *
 * Styling:
 * - Uses MaterialTheme for consistent theming
 * - Icon background: Gray with 20% opacity
 * - Text styles: titleMedium for count, bodyMedium for label
 * - Label color: onSurfaceVariant from MaterialTheme
 */

@Composable
fun StatItem(
    count: String,
    label: String,
    icon: ImageVector
) {
    val countText = if(count.isNotEmpty()) count.plus("+") else "0"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = icon.toString(),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = countText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}