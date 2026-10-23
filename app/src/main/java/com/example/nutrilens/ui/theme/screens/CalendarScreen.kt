package com.example.nutrilens.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: SharedViewModel, onAddFood: () -> Unit) {
    // Храним месяц, который сейчас отображается на экране
    var currentMonthView by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = currentMonthView.lengthOfMonth()
    val firstDayOfWeek = currentMonthView.atDay(1).dayOfWeek.value // 1 (Пн) - 7 (Вс)

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F141A))) {
        // Шапка с переключателем месяцев
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonthView = currentMonthView.minusMonths(1) }) { Text("<", color = Color.White, fontSize = 24.sp) }

            val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
            Text(currentMonthView.format(monthFormatter).replaceFirstChar { it.uppercase() }, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            IconButton(onClick = { currentMonthView = currentMonthView.plusMonths(1) }) { Text(">", color = Color.White, fontSize = 24.sp) }
        }

        // Дни недели
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                Text(it, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Сетка дней
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            var currentDay = 1
            for (row in 0..5) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    for (col in 1..7) {
                        if (row == 0 && col < firstDayOfWeek) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else if (currentDay <= daysInMonth) {
                            val dateStr = currentMonthView.atDay(currentDay).toString()
                            val isSelected = viewModel.selectedDate == dateStr

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp)
                                    .background(if (isSelected) Color(0xFF2ECC71) else Color.Transparent, CircleShape)
                                    .clickable { viewModel.selectedDate = dateStr },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(currentDay.toString(), color = if (isSelected) Color.Black else Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                            }
                            currentDay++
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                if (currentDay > daysInMonth) break
            }
        }

        // Название выбранного дня
        val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale("ru"))
        val parsedDate = try { LocalDate.parse(viewModel.selectedDate) } catch (e: Exception) { LocalDate.now() }

        Text("Дневник за ${parsedDate.format(formatter)}", color = Color(0xFF2ECC71), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        DiaryContent(viewModel, onAddFood)
    }
}