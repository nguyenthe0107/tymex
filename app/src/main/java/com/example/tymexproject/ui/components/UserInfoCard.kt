package com.example.tymexproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.designsystem.component.MySpacer
import com.example.designsystem.dimensions.Padding12
import com.example.designsystem.dimensions.Padding16
import com.example.designsystem.dimensions.Padding4
import com.example.designsystem.dimensions.Padding8
import com.example.designsystem.theme.GrayDEF8
import com.example.domain.model.UserInfoResponse
import java.util.Locale

@Composable
fun UserInfoCard(
    user: UserInfoResponse,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Padding4),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Padding4,
            pressedElevation = Padding4,
            focusedElevation = Padding4,
            hoveredElevation = Padding4,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(Padding12)
    ) {
        Row(
            modifier = Modifier
                .padding(Padding16)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Card(
                shape = RoundedCornerShape(Padding12),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Gray.copy(alpha = 0.1f)
                ),
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = GrayDEF8,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(start = Padding16)
            ) {
                Text(
                    text = user.userName.capitalize(Locale.ROOT),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                )
                MySpacer(height = Padding8)
                HorizontalDivider(thickness = 0.5.dp)
                MySpacer(height = Padding8)
                Text(
                    text = user.htmlUrl,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = Color.Blue,
                )
            }
        }
    }
}