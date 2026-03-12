package com.example.prettypickk02.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.common.reflect.TypeToken

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

data class SearchResult(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val imageUrl: String = ""
)

data class RecentSearch(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(navController: NavHostController) {

    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    val prefs =
        context.getSharedPreferences("recent_search", Context.MODE_PRIVATE)

    val gson = Gson()

    /* ---------- LOAD RECENT ---------- */
    fun loadRecentSearches(): List<RecentSearch> {

        val json = prefs.getString("data", null) ?: return emptyList()

        val type = object : TypeToken<List<RecentSearch>>() {}.type

        return gson.fromJson(json, type)
    }

    /* ---------- SAVE RECENT ---------- */
    fun saveRecentSearches(list: List<RecentSearch>) {
        prefs.edit()
            .putString("data", gson.toJson(list))
            .apply()
    }

    var searchText by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var recentSearches by remember {
        mutableStateOf(loadRecentSearches())
    }
    var isLoading by remember { mutableStateOf(false) }

    /* ---------- ADD RECENT ---------- */
    fun addRecentSearch(item: SearchResult) {

        val newItem = RecentSearch(
            id = item.id,
            name = item.name,
            brand = item.brand,
            imageUrl = item.imageUrl
        )

        val updated =
            listOf(newItem) +
                    recentSearches.filter { it.id != item.id }
                        .take(9)

        recentSearches = updated
        saveRecentSearches(updated)
    }

    /* ---------- SEARCH LOGIC ---------- */
    LaunchedEffect(searchText) {

        if (searchText.trim().isEmpty()) {
            results = emptyList()
            return@LaunchedEffect
        }

        isLoading = true

        firestore.collection("products")
            .get()
            .addOnSuccessListener { snapshot ->

                val query = searchText.lowercase()

                results = snapshot.documents.mapNotNull { doc ->

                    val name = doc.getString("name") ?: return@mapNotNull null
                    val brand = doc.getString("brand") ?: ""
                    val keywords =
                        doc.get("keywords") as? List<String> ?: emptyList()
                    val imageUrl = doc.getString("imageUrl") ?: ""

                    val match =
                        name.lowercase().contains(query) ||
                                brand.lowercase().contains(query) ||
                                keywords.any { it.lowercase().contains(query) }

                    if (match) {
                        SearchResult(
                            id = doc.id,
                            name = name,
                            brand = brand,
                            imageUrl = imageUrl
                        )
                    } else null
                }

                isLoading = false
            }
    }

    /* ---------------- UI ---------------- */

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                title = {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search products, brands…") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {

            when {

                /* ---------- LOADING ---------- */
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                /* ---------- RECENT SEARCHES ---------- */
                searchText.isEmpty() && recentSearches.isNotEmpty() -> {

                    LazyColumn {

                        item {
                            Text(
                                "Recent Searches",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        items(recentSearches) { item ->
                            RecentSearchRow(
                                item = item,
                                onClick = {
                                    navController.navigate(
                                        "product_details/${item.id}"
                                    )
                                }
                            )
                        }
                    }
                }

                /* ---------- NO RESULT ---------- */
                results.isEmpty() -> {
                    Text(
                        "No results found",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }

                /* ---------- SEARCH RESULTS ---------- */
                else -> {
                    LazyColumn {
                        items(results) { item ->
                            SearchItemRow(
                                item = item,
                                onClick = {
                                    addRecentSearch(item)
                                    navController.navigate(
                                        "product_details/${item.id}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchItemRow(
    item: SearchResult,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(Icons.Default.Search, null, tint = Color.Gray)

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {

                Text(item.name, fontWeight = FontWeight.SemiBold)

                Text(
                    item.brand,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Divider(Modifier.padding(top = 10.dp))
    }
}

@Composable
fun RecentSearchRow(
    item: RecentSearch,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(55.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {

            Text(item.name, fontWeight = FontWeight.SemiBold)

            Text(
                item.brand,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Icon(Icons.Default.Search, null, tint = Color.Gray)
    }

    Divider()
}


@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchResultsScreen(navController = rememberNavController())
}