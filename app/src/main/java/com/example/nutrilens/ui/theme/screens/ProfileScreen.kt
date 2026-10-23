package com.example.nutrilens.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(viewModel: SharedViewModel) {
    val context = LocalContext.current
    var newWeightInput by remember { mutableStateOf("") }
    val gained = viewModel.currentWeight - viewModel.startingWeight
    val remaining = viewModel.targetWeight - viewModel.currentWeight

    if (viewModel.showGoalReachedDialog) {
        var selectedNewGoal by remember { mutableStateOf("Поддержание") }
        var newTargetInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.showGoalReachedDialog = false },
            title = { Text("🎉 Цель достигнута!", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Вы достигли желаемого веса. Как планируете двигаться дальше?", color = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    listOf("Поддержание", "Похудение", "Набор массы").forEach { goal ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (goal == selectedNewGoal), onClick = { selectedNewGoal = goal }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2ECC71)))
                            Text(goal, color = Color.White)
                        }
                    }
                    if (selectedNewGoal != "Поддержание") {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTargetInput, onValueChange = { newTargetInput = it }, label = { Text("Новый желаемый вес (кг)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tw = if (selectedNewGoal == "Поддержание") viewModel.currentWeight else newTargetInput.replace(",", ".").toDoubleOrNull() ?: viewModel.currentWeight
                        if (selectedNewGoal != "Поддержание" && (tw < 20 || tw > 200)) {
                            Toast.makeText(context, "Вес должен быть от 20 до 200 кг", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.setNewGoal(selectedNewGoal, tw)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                ) { Text("Применить", color = Color.Black) }
            },
            containerColor = Color(0xFF252525)
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F141A)).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Text("Профиль", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF222831)), shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Цель: ${viewModel.currentGoal}", color = Color(0xFF2ECC71), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text("Текущий вес", color = Color.Gray, fontSize = 14.sp)
                Text("${viewModel.currentWeight} кг", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                Text("BMR: ${viewModel.bmr} ккал | Норма: ${viewModel.calorieGoal} ккал", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            WeightBlock("Начальный Вес", "${viewModel.startingWeight} кг", if(viewModel.currentGoal == "Набор массы") "Набрано: ${String.format("%+.1f", gained)} кг" else "Сброшено: ${String.format("%+.1f", -gained)} кг")
            WeightBlock("Желанный Вес", "${viewModel.targetWeight} кг", if(viewModel.currentGoal == "Поддержание") "Цель достигнута" else "Осталось: ${String.format("%.1f", kotlin.math.abs(remaining))} кг")
        }

        Spacer(Modifier.height(32.dp))
        Text("Обновить вес", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newWeightInput, onValueChange = { newWeightInput = it }, label = { Text("Новый вес (кг)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f).height(60.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF2ECC71))
            )
            Button(
                onClick = {
                    val w = newWeightInput.replace(",", ".").toDoubleOrNull()
                    if (w != null) {
                        if (w < 20 || w > 200) {
                            Toast.makeText(context, "Вес должен быть от 20 до 200 кг", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.updateCurrentWeight(w)
                            newWeightInput = ""
                        }
                    }
                },
                modifier = Modifier.height(60.dp).padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
            ) { Text("Сохранить", color = Color.Black) }
        }
    }
}

@Composable
fun WeightBlock(title: String, weight: String, subtitle: String) {
    Column {
        Text(title, color = Color.Gray, fontSize = 14.sp)
        Card(modifier = Modifier.padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF333A42)), shape = RoundedCornerShape(8.dp)) {
            Text(weight, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
        }
        Text(subtitle, color = Color(0xFF3498DB), fontSize = 12.sp)
    }
}