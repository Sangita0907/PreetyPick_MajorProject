package com.example.prettypickk02.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.prettypickk02.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Peach = Color(0xFFFF8A80)
private val Rose = Color(0xFFF06292)
private val DarkBrown = Color(0xFF4A2C2A)
private val LightBg = Color(0xFFFFF0EC)

@Composable
fun LoginScreen(navController: NavHostController) {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val auth = remember { if (!isPreview) FirebaseAuth.getInstance() else null }
    val db = remember { if (!isPreview) FirebaseFirestore.getInstance() else null }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Peach.copy(alpha = 0.35f), LightBg)
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(70.dp))

            Text(
                text = "PrettyPick",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )

            Text(
                text = "Analyze · Enhance · Shop Beauty",
                fontSize = 13.sp,
                color = DarkBrown.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(modifier = Modifier.padding(24.dp)) {

                    Text(
                        text = "Welcome Back 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null)
                        },
                        placeholder = { Text("Email address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null)
                        },
                        placeholder = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {

                            if (isPreview) return@Button

                            if (email.isBlank() || password.isBlank()) {
                                errorMsg = "Please enter email and password"
                                return@Button
                            }

                            isLoading = true
                            errorMsg = ""

                            auth!!
                                .signInWithEmailAndPassword(
                                    email.trim(),
                                    password
                                )
                                .addOnSuccessListener { result ->

                                    val uid = result.user!!.uid

                                    db!!
                                        .collection("users")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener { doc ->

                                            isLoading = false

                                            val role = doc.getString("role") ?: "user"

                                            if (role == "admin") {

                                                SessionManager.setAdminLoggedIn(
                                                    context,
                                                    true
                                                )

                                                navController.navigate("admin_dashboard") {
                                                    popUpTo("login") { inclusive = true }
                                                }

                                            } else {

                                                SessionManager.setAdminLoggedIn(
                                                    context,
                                                    false
                                                )

                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            }
                                        }
                                        .addOnFailureListener {

                                            isLoading = false
                                            errorMsg = "Failed to fetch user role"
                                        }
                                }
                                .addOnFailureListener {

                                    isLoading = false
                                    errorMsg = it.message ?: "Login failed"
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Rose)
                    ) {

                        Text(
                            text = if (isLoading) "Signing in..." else "Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (errorMsg.isNotEmpty()) {

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = errorMsg,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row {

                Text("New here? ", color = DarkBrown)

                Text(
                    text = "Create account",
                    color = Rose,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("signup")
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}