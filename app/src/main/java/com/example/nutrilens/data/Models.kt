package com.example.nutrilens.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Продукт в базе
@Entity
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int, // на 100г
    val protein: Double,
    val fat: Double,
    val carbs: Double,
    val barcode: String? = null
)

// Запись в дневнике (что съели сегодня)
@Entity
data class DailyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val foodName: String,
    val caloriesEaten: Int,
    val date: Long = System.currentTimeMillis()
)