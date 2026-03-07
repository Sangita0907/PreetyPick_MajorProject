package com.example.prettypickk02.navigation
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prettypickk02.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    activity: Activity
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // ---------- SPLASH ----------
        composable("splash") {
            SplashScreen(navController)
        }

        // ---------- LOGIN ----------
        composable("login") {
            LoginScreen(navController)
        }

        // ---------- SIGNUP ----------
        composable("signup") {
            SignupScreen(navController)
        }
    }
}