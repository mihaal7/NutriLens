package com.example.nutrilens.ui.theme.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = viewModel()

    // Стартуем с экрана загрузки (splash)
    NavHost(navController = navController, startDestination = "splash") {

        // Экран загрузки
        composable("splash") {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true } // Удаляем splash из истории
                    }
                }
            )
        }

        // Анкета
        composable("onboarding") {
            OnboardingScreen(sharedViewModel, onNext = {
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true } // Удаляем анкету из истории
                }
            })
        }

        // Главный контейнер (с нижним меню)
        composable("home") {
            HomeScreenContainer(sharedViewModel, onAddFood = { navController.navigate("add_food") })
        }

        // Экран добавления еды (ИИ, база)
        composable("add_food") {
            AddFoodScreen(sharedViewModel, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun HomeScreenContainer(sharedViewModel: SharedViewModel, onAddFood: () -> Unit) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF161A1E)) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Дневник") },
                    selected = currentDestination?.hierarchy?.any { it.route == "main" } == true,
                    onClick = { bottomNavController.navigate("main") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2ECC71),
                        selectedTextColor = Color(0xFF2ECC71),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Календарь") },
                    selected = currentDestination?.hierarchy?.any { it.route == "calendar" } == true,
                    onClick = { bottomNavController.navigate("calendar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2ECC71),
                        selectedTextColor = Color(0xFF2ECC71),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Я") },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = { bottomNavController.navigate("profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2ECC71),
                        selectedTextColor = Color(0xFF2ECC71),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "main",
            modifier = Modifier.padding(padding)
        ) {
            composable("main") { MainScreen(sharedViewModel, onAddFood) }
            composable("calendar") { CalendarScreen(sharedViewModel, onAddFood) }
            composable("profile") { ProfileScreen(sharedViewModel) }
        }
    }
}