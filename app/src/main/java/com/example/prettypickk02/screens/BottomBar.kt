package com.example.prettypickk02.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

private val CardWhite = Color.White

@Composable
fun BottomBar(
    navController: NavHostController,
    selectedItem: String
) {
    NavigationBar(containerColor = CardWhite) {

        // HOME
        NavigationBarItem(
            selected = selectedItem == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        // CATEGORIES
        NavigationBarItem(
            selected = selectedItem == "categories",
            onClick = {
                navController.navigate("categories") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.GridView, null) },
            label = { Text("Categories") }
        )

        // ACCOUNT
        NavigationBarItem(
            selected = selectedItem == "account",
            onClick = {
                navController.navigate("account") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Account") }
        )

        // ORDERS
        NavigationBarItem(
            selected = selectedItem == "orders",
            onClick = {
                navController.navigate("order_history") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(Icons.Filled.ReceiptLong, null) },
            label = { Text("Order") }
        )
    }
}
