package com.example.prettypickk02.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.prettypickk02.R
import com.example.prettypickk02.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    if (!isPreview) {
        val auth = remember { FirebaseAuth.getInstance() }

        LaunchedEffect(Unit) {
            delay(2500)

            when {

                // ADMIN SESSION
                SessionManager.isAdminLoggedIn(context) -> {
                    navController.navigate("admin_dashboard") {
                        popUpTo("splash") { inclusive = true }
                    }
                }

                // USER SESSION
                auth.currentUser != null -> {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }

                // NO SESSION
                else -> {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F0EB)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.pl),
            contentDescription = "PrettyPick Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navController = rememberNavController())
}