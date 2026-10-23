package com.example.nutrilens.ui.theme.screens

import android.content.Context
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import org.json.JSONArray

data class FoodMacroItem(val name: String, val calories: Int, val protein: Int, val fat: Int, val carbs: Int)

fun loadFoodsFromAssets(context: Context): List<FoodMacroItem> {
    // Встроенная резервная база на случай, если файла JSON нет
    val fallbackBase = listOf(
        FoodMacroItem("Гречневая каша (отварная)", 132, 4, 1, 25),
        FoodMacroItem("Рис белый (отварной)", 116, 2, 0, 25),
        FoodMacroItem("Куриное филе (запеченное)", 148, 28, 3, 0),
        FoodMacroItem("Яйцо вареное", 155, 13, 11, 1),
        FoodMacroItem("Творог 5%", 121, 17, 5, 1),
        FoodMacroItem("Банан", 95, 1, 0, 21),
        FoodMacroItem("Огурец", 15, 0, 0, 2),
        FoodMacroItem("Помидор", 20, 0, 0, 4)
    )

    return try {
        val jsonString = context.assets.open("food_db.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<FoodMacroItem>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.add(FoodMacroItem(obj.getString("name"), obj.getInt("calories"), obj.getInt("protein"), obj.getInt("fat"), obj.getInt("carbs")))
        }
        if (list.isEmpty()) fallbackBase else list
    } catch (e: Exception) {
        fallbackBase // Если файла нет, возвращаем резервную базу
    }
}

@Composable
fun AddFoodScreen(viewModel: SharedViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allFoods = remember { loadFoodsFromAssets(context) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFood by remember { mutableStateOf<FoodMacroItem?>(null) }
    var gramsInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Если поиск пустой - показываем всю базу. Иначе - фильтруем.
    val filteredFoods = if (searchQuery.isEmpty()) {
        allFoods
    } else {
        allFoods.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            isLoading = true
            coroutineScope.launch {
                try {
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, _, _ -> decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE }
                    } else MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                    val model = GenerativeModel("gemini-1.5-flash", "ЗДЕСЬ ВАШ API КОД")
                    val response = model.generateContent(content { image(bitmap); text("Определи блюдо на фото. Ответ дай СТРОГО в таком формате:\nКалории: [число]\nБелки:[число]\nЖиры: [число]\nУглеводы: [число]") })

                    val text = response.text ?: ""
                    val cal = Regex("Калории:\\s*(\\d+)").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    val p = Regex("Белки:\\s*(\\d+)").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    val f = Regex("Жиры:\\s*(\\d+)").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                    val c = Regex("Углеводы:\\s*(\\d+)").find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 0

                    if (cal > 0) {
                        viewModel.addFood(cal, p, f, c)
                        Toast.makeText(context, "ИИ добавил: $cal ккал", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Не удалось распознать. Ответ: $text", Toast.LENGTH_LONG).show()
                    }
                    onBack()
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка ИИ: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                } finally { isLoading = false }
            }
        }
    }

    if (selectedFood != null) {
        AlertDialog(
            onDismissRequest = { selectedFood = null },
            title = { Text(selectedFood!!.name, color = Color.White) },
            text = {
                OutlinedTextField(
                    value = gramsInput,
                    onValueChange = { gramsInput = it },
                    label = { Text("Вес порции (грамм)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val grams = gramsInput.toIntOrNull() ?: 0
                        viewModel.addFood(
                            (selectedFood!!.calories * grams) / 100,
                            (selectedFood!!.protein * grams) / 100,
                            (selectedFood!!.fat * grams) / 100,
                            (selectedFood!!.carbs * grams) / 100
                        )
                        selectedFood = null
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                ) { Text("Добавить", color = Color.Black) }
            },
            dismissButton = { TextButton(onClick = { selectedFood = null }) { Text("Отмена", color = Color.Gray) } },
            containerColor = Color(0xFF252525)
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("В ${viewModel.selectedMealForAdd}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            TextButton(onClick = onBack) { Text("Назад", color = Color(0xFF2ECC71)) }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
        ) {
            Text(if (isLoading) "ИИ думает..." else "Распознать по фото (ИИ)", color = Color.Black, fontSize = 18.sp)
        }
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск продукта") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF2ECC71))
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(items = filteredFoods) { food ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { selectedFood = food },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(food.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text("${food.calories} ккал | Б: ${food.protein}г | Ж: ${food.fat}г | У: ${food.carbs}г (на 100г)", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}