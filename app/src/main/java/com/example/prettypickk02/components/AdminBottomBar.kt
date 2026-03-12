package com.example.prettypickk02.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun AdminBottomBar(navController: NavController) {

    NavigationBar(containerColor = Color.White) {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("add_product") },
            icon = { Icon(Icons.Default.Add, null) },
            label = { Text("Add") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("users") },
            icon = { Icon(Icons.Default.People, null) },
            label = { Text("Users") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("orders") },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Orders") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("all_products") },
            icon = { Icon(Icons.Default.Inventory, null) },
            label = { Text("Products") }
        )
    }
}