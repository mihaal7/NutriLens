package com.example.nutrilens.utils

object CalculatorUtils {
    data class NutriPlan(val bmr: Int, val target: Int)

    fun calculatePlan(weight: Double, height: Double, age: Int, activityMultiplier: Double, goal: String): NutriPlan {
        val bmr = (10 * weight) + (6.25 * height) - (5 * age) + 5
        val maintenance = bmr * activityMultiplier
        val target = when (goal) {
            "Похудение" -> maintenance - 500
            "Набор массы" -> maintenance + 500
            else -> maintenance
        }
        return NutriPlan(bmr.toInt(), target.toInt())
    }
}