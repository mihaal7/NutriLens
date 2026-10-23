package com.example.nutrilens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.nutrilens.ui.theme.NutriLensTheme
import com.example.nutrilens.ui.theme.screens.NavGraph
import com.example.nutrilens.utils.ReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Инициализация системы напоминаний
        setupDailyReminders()

        setContent {
            NutriLensTheme {
                NavGraph()
            }
        }
    }

    private fun setupDailyReminders() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(3, TimeUnit.HOURS)
            .setInputData(workDataOf(
                "title" to "NutriLens",
                "message" to "Пора выпить воды и заглянуть в дневник питания"
            ))
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "nutri_reminder_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}