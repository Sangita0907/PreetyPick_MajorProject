package com.example.prettypickk02.navigation
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        // ---------- CATEGORIES ----------
        composable("categories") {
            CategoriesScreen(navController)
        }
        // ---------- SUB CATEGORY ----------
        composable(
            route = "subcategory/{title}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            SubCategoryScreen(
                navController = navController,
                title = backStackEntry.arguments?.getString("title") ?: ""
            )
        }
        // ---------- HOME ----------
        composable("home") {
            HomeScreen(navController)
        }
        // ---------- SEARCH (NO QUERY) ----------
        composable("search") {
            SearchResultsScreen(navController)
        }

        // ---------- SEARCH (WITH QUERY) ----------
        composable(
            route = "search/{query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            SearchResultsScreen(navController)
        }
        // ---------- ADMIN ----------
        composable("admin_dashboard") {
            AdminDashboardScreen(navController)
        }
        // ---------- ACCOUNT ----------
        composable("account") {
            AccountScreen(navController)
        }

    }


}