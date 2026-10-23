package com.example.nutrilens.ui.theme.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nutrilens.utils.CalculatorUtils
import java.time.LocalDate

data class DayLog(
    val breakfastCals: Int = 0,
    val lunchCals: Int = 0,
    val dinnerCals: Int = 0,
    val snackCals: Int = 0,
    val consumedCalories: Int = 0,
    val consumedProtein: Int = 0,
    val consumedFat: Int = 0,
    val consumedCarbs: Int = 0,
    val waterConsumedMl: Int = 0
)

class SharedViewModel : ViewModel() {
    var userHeight by mutableDoubleStateOf(0.0)
    var userAge by mutableIntStateOf(0)
    var userActivity by mutableDoubleStateOf(1.2)
    var currentGoal by mutableStateOf("")

    var startingWeight by mutableDoubleStateOf(0.0)
    var currentWeight by mutableDoubleStateOf(0.0)
    var targetWeight by mutableDoubleStateOf(0.0)

    var bmr by mutableIntStateOf(1697)
    var calorieGoal by mutableIntStateOf(2500)
    var waterGoalMl by mutableIntStateOf(2000)

    var proteinGoal by mutableIntStateOf(100)
    var fatGoal by mutableIntStateOf(80)
    var carbsGoal by mutableIntStateOf(250)

    var reminderIntervalHours by mutableIntStateOf(3)
    var notificationsEnabled by mutableStateOf(false)
    var showGoalReachedDialog by mutableStateOf(false)

    // БЕРЕМ НАСТОЯЩУЮ ДАТУ СИСТЕМЫ СМАРТФОНА
    var selectedDate by mutableStateOf(LocalDate.now().toString())
    var selectedMealForAdd by mutableStateOf("")

    val logs = mutableStateMapOf<String, DayLog>()

    fun getCurrentLog(): DayLog {
        if (!logs.containsKey(selectedDate)) logs[selectedDate] = DayLog()
        return logs[selectedDate]!!
    }

    private fun calculateMacros(weight: Double, targetCal: Int, activity: Double) {
        val proteinPerKg = when {
            activity >= 1.7 -> 2.0
            activity >= 1.5 -> 1.8
            activity >= 1.3 -> 1.5
            else -> 1.2
        }
        proteinGoal = (weight * proteinPerKg).toInt()
        fatGoal = ((targetCal * 0.25) / 9).toInt()
        val remainingCals = targetCal - (proteinGoal * 4) - (fatGoal * 9)
        carbsGoal = (remainingCals / 4).coerceAtLeast(0)
    }

    fun setupInitialProfile(startW: Double, targetW: Double, height: Double, age: Int, activity: Double, targetCal: Int, calcBmr: Int, goal: String) {
        if (startingWeight == 0.0) startingWeight = startW
        currentWeight = startW
        targetWeight = targetW
        userHeight = height
        userAge = age
        userActivity = activity
        currentGoal = goal
        calorieGoal = targetCal
        bmr = calcBmr
        waterGoalMl = (startW * 33).toInt()
        calculateMacros(startW, targetCal, activity)
    }

    fun updateCurrentWeight(newWeight: Double) {
        val oldWeight = currentWeight
        currentWeight = newWeight
        waterGoalMl = (newWeight * 33).toInt()

        if (currentGoal == "Набор массы" && newWeight >= targetWeight && oldWeight < targetWeight) {
            showGoalReachedDialog = true
        } else if (currentGoal == "Похудение" && newWeight <= targetWeight && oldWeight > targetWeight) {
            showGoalReachedDialog = true
        }
    }

    fun setNewGoal(newGoal: String, newTargetWeight: Double) {
        currentGoal = newGoal
        targetWeight = newTargetWeight
        startingWeight = currentWeight

        val plan = CalculatorUtils.calculatePlan(currentWeight, userHeight, userAge, userActivity, newGoal)
        bmr = plan.bmr
        calorieGoal = plan.target
        calculateMacros(currentWeight, plan.target, userActivity)
        showGoalReachedDialog = false
    }

    fun addFood(calories: Int, protein: Int, fat: Int, carbs: Int) {
        val log = getCurrentLog()
        logs[selectedDate] = log.copy(
            consumedCalories = log.consumedCalories + calories,
            consumedProtein = log.consumedProtein + protein,
            consumedFat = log.consumedFat + fat,
            consumedCarbs = log.consumedCarbs + carbs,
            breakfastCals = if (selectedMealForAdd == "Завтрак") log.breakfastCals + calories else log.breakfastCals,
            lunchCals = if (selectedMealForAdd == "Обед") log.lunchCals + calories else log.lunchCals,
            dinnerCals = if (selectedMealForAdd == "Ужин") log.dinnerCals + calories else log.dinnerCals,
            snackCals = if (selectedMealForAdd == "Перекус/Другое") log.snackCals + calories else log.snackCals
        )
    }

    fun addWater(amount: Int) {
        val log = getCurrentLog()
        logs[selectedDate] = log.copy(waterConsumedMl = log.waterConsumedMl + amount)
    }
}