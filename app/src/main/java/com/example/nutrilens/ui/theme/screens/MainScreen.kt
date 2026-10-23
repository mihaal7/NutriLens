package com.example.nutrilens.ui.theme.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MainScreen(viewModel: SharedViewModel, onAddFood: () -> Unit) {
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        viewModel.notificationsEnabled = isGranted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !viewModel.notificationsEnabled) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F141A))) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Форматирование реальной даты
            val formatter = DateTimeFormatter.ofPattern("dd MMMM", Locale("ru"))
            val parsedDate = try { LocalDate.parse(viewModel.selectedDate) } catch (e: Exception) { LocalDate.now() }
            val displayStr = parsedDate.format(formatter)
            val titleText = if (viewModel.selectedDate == LocalDate.now().toString()) "Сегодня, $displayStr" else displayStr

            Text(titleText, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        DiaryContent(viewModel, onAddFood)
    }
}

// ЭТОТ БЛОК ОСТАЕТСЯ БЕЗ ИЗМЕНЕНИЙ И ВЫЗЫВАЕТСЯ И В КАЛЕНДАРЕ, И В ГЛАВНОМ ОКНЕ
@Composable
fun DiaryContent(viewModel: SharedViewModel, onAddFood: () -> Unit) {
    val log = viewModel.getCurrentLog()
    var customWaterInput by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                val progress = if (viewModel.calorieGoal > 0) log.consumedCalories.toFloat() / viewModel.calorieGoal.toFloat() else 0f
                Canvas(modifier = Modifier.size(180.dp)) {
                    drawCircle(color = Color(0xFF252525), style = Stroke(width = 14.dp.toPx()))
                    drawArc(color = Color(0xFF2ECC71), startAngle = -90f, sweepAngle = 360f * progress, useCenter = false, style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val remaining = (viewModel.calorieGoal - log.consumedCalories).coerceAtLeast(0)
                    Text("$remaining", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("ккал осталось", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF222831))) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        MacroProgress(modifier = Modifier.weight(1f), name = "Белки", consumed = log.consumedProtein, goal = viewModel.proteinGoal, color = Color(0xFF3498DB))
                        MacroProgress(modifier = Modifier.weight(1f), name = "Жиры", consumed = log.consumedFat, goal = viewModel.fatGoal, color = Color(0xFFE74C3C))
                        MacroProgress(modifier = Modifier.weight(1f), name = "Углеводы", consumed = log.consumedCarbs, goal = viewModel.carbsGoal, color = Color(0xFFF1C40F))
                    }
                }
            }
        }

        item { MealCard("Завтрак", log.breakfastCals, "☀️") { viewModel.selectedMealForAdd = "Завтрак"; onAddFood() } }
        item { MealCard("Обед", log.lunchCals, "❄️") { viewModel.selectedMealForAdd = "Обед"; onAddFood() } }
        item { MealCard("Ужин", log.dinnerCals, "🌅") { viewModel.selectedMealForAdd = "Ужин"; onAddFood() } }
        item { MealCard("Перекус/Другое", log.snackCals, "🌙") { viewModel.selectedMealForAdd = "Перекус/Другое"; onAddFood() } }

        item {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF222831)), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("💧 Трекер Воды", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("${log.waterConsumedMl} / ${viewModel.waterGoalMl} мл", color = Color(0xFF2ECC71), fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customWaterInput, onValueChange = { customWaterInput = it },
                            label = { Text("Ввести мл") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).height(60.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF2ECC71))
                        )
                        Button(
                            onClick = {
                                val amount = customWaterInput.toIntOrNull() ?: 0
                                viewModel.addWater(amount)
                                customWaterInput = ""
                            },
                            modifier = Modifier.height(60.dp).padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                        ) { Text("Добавить", color = Color.Black) }
                    }

                    if (viewModel.notificationsEnabled) {
                        Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFF333A42))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Уведомления каждые ${viewModel.reminderIntervalHours} ч.", color = Color.Gray, fontSize = 12.sp)
                            Row {
                                IconButton(onClick = { if (viewModel.reminderIntervalHours > 1) viewModel.reminderIntervalHours-- }) { Text("-", color = Color.White) }
                                IconButton(onClick = { if (viewModel.reminderIntervalHours < 12) viewModel.reminderIntervalHours++ }) { Text("+", color = Color.White) }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun MacroProgress(modifier: Modifier = Modifier, name: String, consumed: Int, goal: Int, color: Color) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(name, color = Color.Gray, fontSize = 12.sp)
        LinearProgressIndicator(progress = if (goal > 0) (consumed.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).height(6.dp), color = color, trackColor = Color.DarkGray)
        Text("$consumed / $goal г", color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun MealCard(title: String, cals: Int, icon: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222831)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
                Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (cals > 0) Text("$cals ккал", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(end = 12.dp))
                Text("+", color = Color(0xFF2ECC71), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}