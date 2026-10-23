package com.example.nutrilens.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateNext: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }

    // Анимация загрузки на 2.5 секунды
    LaunchedEffect(Unit) {
        for (i in 1..100) {
            delay(20)
            progress = i / 100f
        }
        delay(300)
        onNavigateNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0C0E)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Калории",
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Считай. Питайся. Достигай.",
            fontSize = 16.sp,
            color = Color(0xFF2ECC71)
        )

        Spacer(modifier = Modifier.height(80.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF2ECC71), modifier = Modifier.size(28.dp))
            Text("  . . . .  ", color = Color(0xFF2ECC71), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2ECC71), modifier = Modifier.size(32.dp))
            Text("  . . . .  ", color = Color(0xFF2ECC71), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF2ECC71), modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Загрузка...",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .width(220.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF2ECC71),
            trackColor = Color(0xFF252525),
            strokeCap = StrokeCap.Round
        )
    }
}