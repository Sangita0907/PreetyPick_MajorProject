package com.example.prettypickk02.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.prettypickk02.R

// ---------- COLORS ----------
private val ScreenBg = Color(0xFFFFF3EF)
private val TextDark = Color(0xFF252525)

// ---------- DATA MODEL ----------
data class CategoryItem(
    val name: String,
    val image: Int
)

// ---------- SCREEN ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavHostController) {

    val categories = listOf(
        CategoryItem("Makeup", R.drawable.makeup),
        CategoryItem("Skin", R.drawable.skin),
        CategoryItem("Hair", R.drawable.hair),
        CategoryItem("Bath & Body", R.drawable.bath),
        CategoryItem("Fragrances", R.drawable.perfume2),

        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Categories",
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                selectedItem = "categories"
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(ScreenBg)
        ) {
            items(categories) { category ->
                CategoryCard(
                    category = category,
                    navController = navController
                )
            }
        }
    }
}

// ---------- CATEGORY CARD ----------
@Composable
fun CategoryCard(
    category: CategoryItem,
    navController: NavHostController
) {

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFC1D9),
            Color(0xFFFFE4EE)
        )
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clickable {
                navController.navigate("subcategory/${category.name}")
            },
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = category.name,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.weight(1f)
            )

            Image(
                painter = painterResource(category.image),
                contentDescription = category.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ---------- PREVIEW ----------
@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    CategoriesScreen(navController = rememberNavController())
}
