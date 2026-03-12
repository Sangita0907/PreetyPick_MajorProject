package com.example.prettypickk02.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.prettypickk02.ui.theme.AppBg
import com.example.prettypickk02.ui.theme.CardWhite
import com.example.prettypickk02.ui.theme.AccentPink
import com.example.prettypickk02.ui.theme.SoftLavender


val TextMuted = Color(0xFF757575)
private val TextDark = Color(0xFF252525)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavHostController) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    val uid = auth.currentUser?.uid

    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf(auth.currentUser?.email ?: "") }

    /* 🔄 REAL-TIME USER DATA */
    DisposableEffect(uid) {
        if (uid == null) return@DisposableEffect onDispose {}

        val listener = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { doc, _ ->
                if (doc != null && doc.exists()) {
                    userName = doc.getString("username") ?: "User"
                    userEmail = doc.getString("email") ?: userEmail
                    profileImageUrl = doc.getString("profileImageUrl")
                }
            }

        onDispose { listener.remove() }
    }

    /* 🖼 IMAGE PICKER */
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null && uid != null) {
                profileImageUri = uri
                val storageRef =
                    storage.reference.child("profile_images/$uid.jpg")

                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            firestore.collection("users")
                                .document(uid)
                                .update("profileImageUrl", downloadUrl.toString())
                        }
                    }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = TextDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite
                )
            )
        },
        bottomBar = {
            BottomBar(navController = navController, selectedItem = "account")
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(AppBg)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ){

            /* 👤 PROFILE HEADER */
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(modifier = Modifier.size(88.dp)) {

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(1.dp, SoftLavender, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            profileImageUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(profileImageUri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            profileImageUrl != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(profileImageUrl),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    tint = TextMuted,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(4.dp, 4.dp)
                            .background(AccentPink, CircleShape)
                            .padding(6.dp)
                            .size(18.dp)
                            .clickable { imagePicker.launch("image/*") }
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(userName, color = TextDark, style = MaterialTheme.typography.titleMedium)
                    Text(userEmail, color = TextMuted, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileActionCard(Icons.Default.Person, "Edit Profile") {
                navController.navigate("my_account")
            }

            // ✅ ADDRESS CLICK FIXED
            ProfileActionCard(Icons.Default.LocationOn, "Address") {
                navController.navigate("address_list")
            }

            ProfileActionCard(Icons.Default.Notifications, "Notifications") {}

            // ✅ ORDERS CLICK FIXED
            ProfileActionCard(Icons.Default.ShoppingBag, "Orders") {
                navController.navigate("order_history")
            }

            ProfileActionCard(Icons.Default.Payment, "Payment") {
                navController.navigate("payment_screen")
            }

            ProfileActionCard(Icons.Default.Favorite, "Wishlist") {
                navController.navigate("wishlist")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SoftLavender),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Logout, tint = AccentPink, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Logout", color = AccentPink)
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    auth.signOut()
                    navController.navigate("login") { popUpTo(0) }
                }) {
                    Text("Yes", color = AccentPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, tint = AccentPink, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, modifier = Modifier.weight(1f), color = TextDark)
            Icon(Icons.Default.KeyboardArrowRight, tint = AccentPink, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen(navController = rememberNavController())
}
