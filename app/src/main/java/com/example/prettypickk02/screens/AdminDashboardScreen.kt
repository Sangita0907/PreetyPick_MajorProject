package com.example.prettypickk02.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prettypickk02.components.AdminBottomBar
import com.example.prettypickk02.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    rootNavController: NavHostController
) {

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val adminNavController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Admin Menu",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )

                NavigationDrawerItem(
                    label = { Text("Categories") },
                    selected = false,
                    onClick = { }
                )

                NavigationDrawerItem(
                    label = { Text("Analytics") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {

                            SessionManager.clearSession(context)
                            FirebaseAuth.getInstance().signOut()

                            rootNavController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }

                        }) {
                            Icon(Icons.Default.Logout, null)
                        }
                    }
                )
            },
            bottomBar = {
                AdminBottomBar(adminNavController)
            }
        ) { padding ->

            NavHost(
                navController = adminNavController,
                startDestination = "add_product",
                modifier = Modifier.padding(padding)
            ) {

                composable("add_product") {
                    AddProductScreen()
                }

                composable("users") {
                    UsersScreen()
                }

                composable("orders") {
                    OrdersScreen()
                }

                composable("all_products") {
                    AllProductsScreen()
                }
                }
            }
        }
    }



