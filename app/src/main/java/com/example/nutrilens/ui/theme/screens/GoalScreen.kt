package com.example.nutrilens.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoalScreen(onFinish: () -> Unit) {
    val goals = listOf("Похудение", "Удержание веса", "Набор массы")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Выберите вашу цель", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(40.dp))

        goals.forEach { goal ->
            Button(
                onClick = { onFinish() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252525)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2ECC71))
            ) {
                Text(goal, color = Color(0xFF2ECC71), fontSize = 18.sp)
            }
        }
    }
}