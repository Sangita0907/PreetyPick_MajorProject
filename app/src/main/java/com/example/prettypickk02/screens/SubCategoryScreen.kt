package com.example.prettypickk02.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prettypickk02.ui.theme.AccentPink


private val ScreenBg = Color(0xFFFFF3EF)
private val CardBg = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF2E2E2E)
private val DividerColor = Color(0xFFF2C9D6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryScreen(
    navController: NavHostController,
    title: String
) {

    val items = when (title) {

        "Makeup" -> listOf(
            "Complete Makeup Collection",
            "Face",
            "Eyes",
            "Lips",
            "Multi-Functional Makeup Palettes",
            "Makeup Kits & Combos"
        )

        "Skin" -> listOf(
            "Complete Skin Collection",
            "Face Wash",
            "Cleansers",
            "Serums",
            "Moisturizers",
            "Sun Care",
            "Shop Toners & Mists",
            "Masks",
            "Lip Care",
            "Eye Care",
            "Specialised Skincare"
        )

        "Hair" -> listOf(
            "Complete Hair Collection",
            "Hair Care",
            "Hair Styling",
            "Shop By Hair Type"

        )

        "Bath & Body" -> listOf(
            "Complete Bath & Body Collection",
            "Bath & Shower",
            "Body Care",
            "Feminine Hygiene",
            "Female Grooming",
            "Hands & Feet"
        )

        "Fragrances" -> listOf(
            "Complete Fragrance Collection",
            "Womens Fragrance",
            "Mens Fragrance",
            "Premium Fragrance",
        )



        else -> emptyList()
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDark
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {

            items(items) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            // ✅ SAFE NAVIGATION (ENCODED)
                            navController.navigate(
                                "products/${Uri.encode(title)}/${Uri.encode(item)}"
                            )
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = item,
                            fontWeight = if (item.startsWith("Complete"))
                                FontWeight.SemiBold
                            else FontWeight.Medium,
                            color = if (item.startsWith("Complete"))
                                AccentPink
                            else TextDark
                        )

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = AccentPink
                        )
                    }
                }

                Divider(
                    color = DividerColor,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}
