package com.example.prettypickk02.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.prettypickk02.data.WishlistCartRepository
import com.example.prettypickk02.model.Product
import com.example.prettypickk02.utils.RecentlyViewedManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

private val ScreenBg = Color(0xFFFFF1F5)
private val AccentPink = Color(0xFFE91E63)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: String
) {

    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var product by remember { mutableStateOf<Product?>(null) }
    var isWishlisted by remember { mutableStateOf(false) }
    var isAddedToBag by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {

        firestore.collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener {

                val fetchedProduct = it.toObject(Product::class.java)

                product = fetchedProduct

                fetchedProduct?.let { p ->

                    // SAVE TO RECENTLY VIEWED
                    RecentlyViewedManager.saveProduct(context, p)

                    // INCREASE PRODUCT VIEW COUNT
                    firestore.collection("products")
                        .document(productId)
                        .update("views", FieldValue.increment(1))
                }
            }

        uid?.let { userId ->

            firestore.collection("users")
                .document(userId)
                .collection("wishlist")
                .document(productId)
                .get()
                .addOnSuccessListener {
                    isWishlisted = it.exists()
                }

            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document(productId)
                .get()
                .addOnSuccessListener {
                    isAddedToBag = it.exists()
                }
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {

            product?.let { item ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    OutlinedButton(
                        onClick = {
                            isWishlisted = !isWishlisted

                            if (isWishlisted) {
                                WishlistCartRepository.addToWishlist(
                                    productId = productId,
                                    name = item.name,
                                    price = item.price,
                                    imageUrl = item.imageUrl
                                )
                                Toast.makeText(context, "Added to Wishlist 💖", Toast.LENGTH_SHORT).show()
                            } else {
                                WishlistCartRepository.removeFromWishlist(productId)
                                Toast.makeText(context, "Removed from Wishlist ", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = AccentPink
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (isWishlisted) "Wishlisted" else "Wishlist")
                    }

                    Button(
                        onClick = {
                            if (!isAddedToBag) {
                                WishlistCartRepository.addToCart(
                                    productId = productId,
                                    name = item.name,
                                    price = item.price,
                                    imageUrl = item.imageUrl
                                )
                                isAddedToBag = true
                                Toast.makeText(context, "Added to Bag 🛒", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text(if (isAddedToBag) "Added" else "Add to Bag", color = Color.White)
                    }
                }
            }
        }
    ) { padding ->

        product?.let {

            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                AsyncImage(
                    model = it.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(300.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(it.brand, color = Color.Gray)
                Text(it.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                Spacer(Modifier.height(8.dp))

                Text(
                    "₹${it.price}",
                    fontWeight = FontWeight.Bold,
                    color = AccentPink,
                    fontSize = 20.sp
                )

                Spacer(Modifier.height(12.dp))

                Text("Shade: ${it.shade}")
                Spacer(Modifier.height(6.dp))
                Text("Suitable for: ${it.skinType} skin")

                Spacer(Modifier.height(12.dp))

                Text(it.description, color = Color.DarkGray)
            }
        }
    }
}