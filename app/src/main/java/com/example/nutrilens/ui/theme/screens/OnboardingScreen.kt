package com.example.nutrilens.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.nutrilens.utils.CalculatorUtils

@Composable
fun OnboardingScreen(viewModel: SharedViewModel, onNext: () -> Unit) {
    val context = LocalContext.current
    var weight by remember { mutableStateOf("") }
    var targetWeightInput by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    val activityOptions = listOf("Минимальная" to 1.2, "Умеренная" to 1.375, "Средняя" to 1.55, "Высокая" to 1.725)
    var selectedActivity by remember { mutableStateOf(activityOptions[0]) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(24.dp).verticalScroll(rememberScrollState())) {
        Text("Ваши параметры", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(24.dp))

        val colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF2ECC71))

        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Текущий вес (кг)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), colors = colors)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = targetWeightInput, onValueChange = { targetWeightInput = it }, label = { Text("Желаемый вес (кг)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), colors = colors)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Рост (см)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth(), colors = colors)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Возраст") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = colors)

        Spacer(Modifier.height(24.dp))
        Text("Уровень активности", fontWeight = FontWeight.SemiBold, color = Color.White)

        activityOptions.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = (option == selectedActivity), onClick = { selectedActivity = option })
                Text(option.first, color = Color.LightGray)
            }
        }

        Button(
            onClick = {
                val w = weight.replace(",", ".").toDoubleOrNull() ?: 0.0
                val tw = targetWeightInput.replace(",", ".").toDoubleOrNull() ?: 0.0
                val h = height.replace(",", ".").toDoubleOrNull() ?: 0.0
                val a = age.toIntOrNull() ?: 0

                // ВАЛИДАЦИЯ ВВОДА
                if (w < 20 || w > 300) { Toast.makeText(context, "Вес должен быть от 20 до 300 кг", Toast.LENGTH_SHORT).show(); return@Button }
                if (tw < 20 || tw > 200) { Toast.makeText(context, "Желаемый вес должен быть от 20 до 200 кг", Toast.LENGTH_SHORT).show(); return@Button }
                if (h < 100 || h > 230) { Toast.makeText(context, "Рост должен быть от 100 до 230 см", Toast.LENGTH_SHORT).show(); return@Button }
                if (a < 10 || a > 110) { Toast.makeText(context, "Возраст должен быть от 10 до 110 лет", Toast.LENGTH_SHORT).show(); return@Button }

                val goalStr = if(tw > w) "Набор массы" else if(tw < w) "Похудение" else "Поддержание"
                val plan = CalculatorUtils.calculatePlan(w, h, a, selectedActivity.second, goalStr)

                viewModel.setupInitialProfile(w, tw, h, a, selectedActivity.second, plan.target, plan.bmr, goalStr)
                onNext()
            },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
        ) { Text("Начать", color = Color.Black, fontSize = 18.sp) }
    }
}