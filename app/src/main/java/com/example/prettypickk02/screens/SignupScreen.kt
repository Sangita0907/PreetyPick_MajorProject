package com.example.prettypickk02.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.prettypickk02.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

private val Peach = Color(0xFFFF8A80)
private val Rose = Color(0xFFF06292)
private val DarkBrown = Color(0xFF4A2C2A)
private val LightBg = Color(0xFFFFF0EC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {

    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val auth = remember { if (!isPreview) FirebaseAuth.getInstance() else null }
    val db = remember { if (!isPreview) FirebaseFirestore.getInstance() else null }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var dob by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Peach.copy(0.3f), LightBg))
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(50.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )

            Text(
                text = "Join PrettyPick today",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(26.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {

                Column(modifier = Modifier.padding(22.dp)) {

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Gender",
                        fontWeight = FontWeight.SemiBold,
                        color = DarkBrown
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Female", "Male", "Other").forEach {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = gender == it,
                                    onClick = { gender = it },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Rose
                                    )
                                )
                                Text(it)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dob,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date of Birth") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(Modifier.height(26.dp))

                    Button(
                        onClick = {

                            if (isPreview) return@Button

                            when {

                                username.isBlank() || email.isBlank() ||
                                        password.isBlank() || confirmPassword.isBlank() ||
                                        mobile.isBlank() || dob.isBlank() ->
                                    Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()

                                password.length < 6 ->
                                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()

                                password != confirmPassword ->
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()

                                else -> {

                                    loading = true

                                    auth!!.createUserWithEmailAndPassword(email, password)
                                        .addOnSuccessListener {

                                            val uid = auth.currentUser!!.uid

                                            val userData = hashMapOf(
                                                "username" to username,
                                                "email" to email,
                                                "mobile" to mobile,
                                                "gender" to gender,
                                                "dob" to dob,

                                                // Major project fields
                                                "role" to "user",
                                                "totalOrders" to 0,
                                                "lastActive" to System.currentTimeMillis(),
                                                "wishlistCount" to 0
                                            )

                                            db!!.collection("users").document(uid)
                                                .set(userData)
                                                .addOnSuccessListener {

                                                    loading = false

                                                    Toast.makeText(
                                                        context,
                                                        "Account Created",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    navController.navigate("login") {
                                                        popUpTo("signup") { inclusive = true }
                                                    }
                                                }
                                                .addOnFailureListener {

                                                    loading = false

                                                    Toast.makeText(
                                                        context,
                                                        it.message,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                        .addOnFailureListener {

                                            loading = false

                                            Toast.makeText(
                                                context,
                                                it.message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Rose)
                    ) {

                        Text(
                            text = if (loading) "Creating..." else "Create Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row {

                Text("Already have an account? ", color = DarkBrown)

                Text(
                    text = "Login",
                    color = Rose,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {

                    datePickerState.selectedDateMillis?.let {
                        dob = SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                        ).format(Date(it))
                    }

                    showDatePicker = false

                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_6
)
@Composable
fun SignupScreenPreview() {
    SignupScreen(navController = rememberNavController())
}