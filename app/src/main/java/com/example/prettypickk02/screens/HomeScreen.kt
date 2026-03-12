package com.example.prettypickk02.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.prettypickk02.R
import com.example.prettypickk02.utils.RecentlyViewedManager
import com.example.prettypickk02.model.Product
import com.example.prettypickk02.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.delay

data class Category(val name: String, val icon: Int)
data class UiProduct(val name: String, val price: String, val image: Int)
data class AddToBagProduct(val name: String, val price: String, val imageUrl: String)
private val TextDark = Color(0xFF252525)
private val AppBg = Color(0xFFFFF1F5)
private val CardWhite = Color.White
private val SoftLavender = Color(0xFFF3E5F5)
private val AccentPink = Color(0xFFE91E63)

@Composable
fun HomeScreen(navController: NavHostController) {

    Scaffold(
        containerColor = AppBg,
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("chatbot") },
                containerColor = Pink80,
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chatbot),
                    contentDescription = "Chatbot",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            OutlinedTextField(
                value = "",
                onValueChange = {},
                enabled = false,
                placeholder = { Text("Search for products", color = TextDark) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { navController.navigate("search") },
                shape = RoundedCornerShape(28.dp)
            )

            LazyColumn {

                item { CategoryRow(navController) }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                item { AutoBanner() }

                item { RecentlyViewedSection() }

                item { TrendingProductsSection() }

                item { SectionTitle("Add to Bag") }

                item { AddToBagRow() }

                item { SectionTitle("Trending Deals") }

                item { DealsGrid() }

                item { SectionTitle("Top Picks For You") }

                item { ProductRow() }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    TopAppBar(
        title = { Text("PrettyPick", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CardWhite),
        actions = {

            IconButton(onClick = { navController.navigate("wishlist") }) {
                Icon(Icons.Default.FavoriteBorder, null)
            }

            IconButton(onClick = { navController.navigate("cart") }) {
                Icon(Icons.Default.ShoppingCart, null)
            }
        }
    )

}

@Composable
fun RecentlyViewedSection() {
    val context = LocalContext.current

    val products = remember {
        RecentlyViewedManager.getProducts(context)
    }

    if (products.isEmpty()) return

    SectionTitle("Recently Viewed")

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {

        items(products) { product ->

            Card(
                modifier = Modifier
                    .width(150.dp)
                    .padding(end = 12.dp),
                shape = RoundedCornerShape(18.dp)
            ) {

                Column {

                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(130.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )

                    Column(Modifier.padding(10.dp)) {

                        Text(product.name, fontWeight = FontWeight.SemiBold)

                        Text(
                            "₹${product.price}",
                            color = AccentPink
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun TrendingProductsSection() {
    val firestore = FirebaseFirestore.getInstance()

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {

        firestore.collection("products")
            .orderBy("views", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { snapshot ->

                products = snapshot.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }
            }
    }

    if (products.isEmpty()) return

    SectionTitle("Trending Products")

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {

        items(products) { product ->

            Card(
                modifier = Modifier
                    .width(150.dp)
                    .padding(end = 12.dp),
                shape = RoundedCornerShape(18.dp)
            ) {

                Column {

                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .height(130.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )

                    Column(Modifier.padding(10.dp)) {

                        Text(product.name, fontWeight = FontWeight.SemiBold)

                        Text(
                            "₹${product.price}",
                            color = AccentPink
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CategoryRow(navController: NavHostController) {

    val categories = listOf(
        Category("Makeup", R.drawable.ic_makeup),
        Category("Skin", R.drawable.ic_skin),
        Category("Hair", R.drawable.ic_hair),
        Category("Bath & Body", R.drawable.ic_bath_body),
        Category("Perfume", R.drawable.ic_perfume)
    )

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {

        items(categories) { cat ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {

                        when (cat.name) {
                            "Makeup" -> navController.navigate("makeup")
                            "Skin" -> navController.navigate("skin")
                            "Hair" -> navController.navigate("hair")
                            "Bath & Body" -> navController.navigate("bath_body")
                            "Perfume" -> navController.navigate("perfume")
                        }
                    }
            ) {

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SoftLavender),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(cat.icon),
                        contentDescription = cat.name,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(cat.name)
            }
        }
    }

}

@Composable
fun AutoBanner() {

    val banners = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3
    )

    var index by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {

        while (true) {
            delay(3500)
            index = (index + 1) % banners.size
        }
    }

    Image(
        painter = painterResource(banners[index]),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(12.dp)
            .clip(RoundedCornerShape(18.dp)),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
}

@Composable
fun ProductRow() {

    val products = listOf(
        UiProduct("Lipstick", "₹599", R.drawable.lipstick),
        UiProduct("Perfume", "₹1299", R.drawable.perfume),
        UiProduct("Face Wash", "₹349", R.drawable.facewash),
        UiProduct("Moisturizer", "₹499", R.drawable.moisturizer)
    )

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(products) { ProductCard(it) }
    }
}

@Composable
fun ProductCard(product: UiProduct) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(end = 12.dp),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column {

            Image(
                painter = painterResource(product.image),
                contentDescription = product.name,
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Column(Modifier.padding(10.dp)) {

                Text(product.name, fontWeight = FontWeight.SemiBold)

                Text(product.price, color = AccentPink)
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {


    NavigationBar {

        NavigationBarItem(true, {}, { Icon(Icons.Default.Home, null) }, label = { Text("Home") })

        NavigationBarItem(false, { navController.navigate("categories") }, { Icon(Icons.Default.GridView, null) }, label = { Text("Categories") })

        NavigationBarItem(false, { navController.navigate("account") }, { Icon(Icons.Default.Person, null) }, label = { Text("Account") })

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("order_history") },
            icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, null)  },
            label = { Text("Order") }
        )
    }


}
// ---------------- ADD TO BAG ----------------
@Composable
fun AddToBagRow() {
    val products = listOf(
        AddToBagProduct("Matte Lipstick", "₹799", "https://marscosmetics.in/cdn/shop/files/1w_cb3d98f0-e737-4482-bbb8-968db141c0a8.jpg"),
        AddToBagProduct("Lip Gloss", "₹499", "https://letshyphen.com/cdn/shop/files/Card_1_dd7485ae-903d-430c-9a2a-2f70e7e63b8f.jpg"),
        AddToBagProduct("Blush", "₹899", "https://www.kaybeauty.com/cdn/shop/files/1_10f321c1-4c19-411e-83f7-b14eb6d32666.jpg"),
        AddToBagProduct("Kajal", "₹299", "https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcQFuLOyf4mY9GPo9dTGd3ny_f9jawSyrnxMSUTjqlGwVfpPs2TqnIjI_bWUgydxCQO8TgzlELWUt9bvUO3pDSyXFROMoN6r9hX_jNE5axZE-WyC4tGhQSGHpw")
    )

    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(products) { AddToBagCard(it) }
    }
}
@Composable
fun AddToBagCard(product: AddToBagProduct) {
    Card(
        modifier = Modifier
            .width(190.dp)
            .padding(end = 12.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(product.name, fontWeight = FontWeight.SemiBold)
            Text(product.price, color = AccentPink)
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Add to Bag")
            }
        }
    }
}
// ---------------- DEALS GRID ----------------
@Composable
fun DealsGrid() {
    val deals = listOf(
        R.drawable.deal1,
        R.drawable.deal2,
        R.drawable.deal3,
        R.drawable.deal4
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .height(360.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(deals) {
            Image(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.clip(RoundedCornerShape(18.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}
