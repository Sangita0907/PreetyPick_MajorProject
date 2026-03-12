package com.example.prettypickk02.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
//  DESIGN TOKENS — Soft Luxury Beauty Theme
// ─────────────────────────────────────────────
private val BgCream       = Color(0xFFFDF8F5)       // warm off-white
private val BgPetal       = Color(0xFFFFF0F5)       // petal pink tint
private val PinkDeep      = Color(0xFFD4457A)       // deep rose — primary CTA
private val PinkMid       = Color(0xFFE8789A)       // medium blush
private val PinkLight     = Color(0xFFFCE4EC)       // very soft blush fill
private val PinkBorder    = Color(0xFFF5C2D3)       // border blush
private val PinkGradStart = Color(0xFFE8527A)
private val PinkGradEnd   = Color(0xFFC2185B)
private val GoldLine      = Color(0xFFE8C98A)       // warm gold accent
private val TextDeep      = Color(0xFF2C1A22)       // near-black rose
private val TextMid       = Color(0xFF8C5D72)       // dusty mauve
private val TextSoft      = Color(0xFFBF9AAD)       // muted hint
private val DividerPink   = Color(0xFFF5D5E2)
private val White         = Color(0xFFFFFFFF)
private val ErrorRed      = Color(0xFFD32F2F)

private val GradientCTA   = Brush.linearGradient(listOf(PinkGradStart, PinkGradEnd))
private val GradientBg    = Brush.verticalGradient(listOf(BgPetal, BgCream, BgCream))

// ─────────────────────────────────────────────
//  SIGNUP SCREEN
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController) {

    val context   = LocalContext.current
    val isPreview = LocalInspectionMode.current

    val auth = remember { if (!isPreview) FirebaseAuth.getInstance() else null }
    val db   = remember { if (!isPreview) FirebaseFirestore.getInstance() else null }

    // ── All original state variables — unchanged ──
    var username        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mobile          by remember { mutableStateOf("") }
    var gender          by remember { mutableStateOf("Female") }
    var dob             by remember { mutableStateOf("") }
    var loading         by remember { mutableStateOf(false) }
    var showDatePicker  by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBg)
    ) {

        // ── Decorative top blobs ──
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(PinkMid.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = 30.dp)
                .clip(CircleShape)
                .background(GoldLine.copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(56.dp))

            // ── Brand mark ──
            BrandMark()

            Spacer(Modifier.height(32.dp))

            // ── Main form card ──
            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(28.dp),
                colors    = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                // Thin gold top border accent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(Color.Transparent, GoldLine, PinkMid, GoldLine, Color.Transparent)))
                )

                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {

                    Text(
                        "Create Account",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextDeep,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Your beauty journey begins here ✦",
                        fontSize = 12.sp,
                        color    = TextSoft,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(Modifier.height(24.dp))

                    // ── Form fields ──
                    BeautyField(username, { username = it }, "Full Name", Icons.Outlined.Person)
                    Spacer(Modifier.height(14.dp))
                    BeautyField(email, { email = it }, "Email Address", Icons.Outlined.Email)
                    Spacer(Modifier.height(14.dp))
                    BeautyField(
                        password, { password = it }, "Password",
                        Icons.Outlined.Lock,
                        isPassword = true
                    )
                    Spacer(Modifier.height(14.dp))
                    BeautyField(
                        confirmPassword, { confirmPassword = it }, "Confirm Password",
                        Icons.Outlined.Lock,
                        isPassword = true
                    )
                    Spacer(Modifier.height(14.dp))
                    BeautyField(mobile, { mobile = it }, "Mobile Number", Icons.Outlined.Phone)

                    Spacer(Modifier.height(22.dp))
                    ThinDivider()
                    Spacer(Modifier.height(20.dp))

                    // ── Gender ──
                    FieldLabel("Gender")
                    Spacer(Modifier.height(10.dp))
                    GenderSelector(gender) { gender = it }

                    Spacer(Modifier.height(20.dp))
                    ThinDivider()
                    Spacer(Modifier.height(20.dp))

                    // ── Date of Birth ──
                    FieldLabel("Date of Birth")
                    Spacer(Modifier.height(10.dp))
                    DobField(dob = dob, onClick = { showDatePicker = true })

                    Spacer(Modifier.height(30.dp))

                    // ── CTA Button ──
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
                                                "username"      to username,
                                                "email"         to email,
                                                "mobile"        to mobile,
                                                "gender"        to gender,
                                                "dob"           to dob,
                                                "role"          to "user",
                                                "totalOrders"   to 0,
                                                "lastActive"    to System.currentTimeMillis(),
                                                "wishlistCount" to 0
                                            )
                                            db!!.collection("users").document(uid)
                                                .set(userData)
                                                .addOnSuccessListener {
                                                    loading = false
                                                    Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
                                                    navController.navigate("login") {
                                                        popUpTo("signup") { inclusive = true }
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    loading = false
                                                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                                }
                                        }
                                        .addOnFailureListener {
                                            loading = false
                                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                        },
                        modifier       = Modifier.fillMaxWidth().height(54.dp),
                        shape          = RoundedCornerShape(16.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        enabled        = !loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    if (loading)
                                        Brush.linearGradient(listOf(PinkBorder, PinkBorder))
                                    else
                                        GradientCTA
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (loading) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = PinkDeep,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text("Creating...", color = PinkDeep, fontWeight = FontWeight.SemiBold)
                                }
                            } else {
                                Text(
                                    "Create Account",
                                    color      = White,
                                    fontSize   = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Login link ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?  ", color = TextMid, fontSize = 13.sp)
                Text(
                    "Sign In",
                    color      = PinkDeep,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 13.sp,
                    modifier   = Modifier.clickable { navController.navigate("login") }
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    // ── Date Picker Dialog — logic unchanged ──
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        dob = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("OK", color = PinkDeep) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ─────────────────────────────────────────────
//  BRAND MARK
// ─────────────────────────────────────────────
@Composable
fun BrandMark() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Logo circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(PinkGradStart, PinkGradEnd))),
            contentAlignment = Alignment.Center
        ) {
            Text("✦", fontSize = 26.sp, color = White)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "PrettyPick",
            fontSize      = 28.sp,
            fontWeight    = FontWeight.ExtraBold,
            color         = TextDeep,
            letterSpacing = (-0.8).sp
        )
        Spacer(Modifier.height(4.dp))
        // Gold underline accent
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(2.dp)
                .background(Brush.horizontalGradient(listOf(Color.Transparent, GoldLine, Color.Transparent)))
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Analyze · Enhance · Shop Beauty",
            fontSize  = 11.sp,
            color     = TextSoft,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────
//  BEAUTY FIELD — styled input
// ─────────────────────────────────────────────
@Composable
fun BeautyField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value               = value,
        onValueChange       = onValueChange,
        label               = { Text(label, fontSize = 13.sp) },
        leadingIcon         = {
            Icon(icon, null, tint = PinkMid, modifier = Modifier.size(20.dp))
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier            = Modifier.fillMaxWidth(),
        shape               = RoundedCornerShape(14.dp),
        singleLine          = true,
        colors              = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = TextDeep,
            unfocusedTextColor      = TextDeep,
            focusedBorderColor      = PinkDeep,
            unfocusedBorderColor    = PinkBorder,
            cursorColor             = PinkDeep,
            focusedLabelColor       = PinkDeep,
            unfocusedLabelColor     = TextSoft,
            focusedLeadingIconColor = PinkDeep,
            focusedContainerColor   = White,
            unfocusedContainerColor = BgCream
        )
    )
}

// ─────────────────────────────────────────────
//  GENDER SELECTOR — pill chips
// ─────────────────────────────────────────────
@Composable
fun GenderSelector(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf("Female", "Male", "Other").forEach { option ->
            val isSelected = selected == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) PinkDeep else PinkLight)
                    .border(
                        1.dp,
                        if (isSelected) PinkDeep else PinkBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(option) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color      = if (isSelected) White else TextMid,
                    fontSize   = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  DOB FIELD — tappable readonly field
// ─────────────────────────────────────────────
@Composable
fun DobField(dob: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BgCream)
            .border(1.dp, PinkBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.CalendarMonth, null,
                tint     = if (dob.isEmpty()) TextSoft else PinkDeep,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text      = if (dob.isEmpty()) "Select date of birth" else dob,
                color     = if (dob.isEmpty()) TextSoft else TextDeep,
                fontSize  = 14.sp,
                fontWeight = if (dob.isEmpty()) FontWeight.Normal else FontWeight.Medium
            )
        }
    }
}

// ─────────────────────────────────────────────
//  HELPERS
// ─────────────────────────────────────────────
@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        color      = TextMid,
        fontSize   = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp
    )
}

@Composable
fun ThinDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color.Transparent, DividerPink, DividerPink, Color.Transparent)
                )
            )
    )
}

// ─────────────────────────────────────────────
//  PREVIEW
// ─────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_6)
@Composable
fun SignupScreenPreview() {
    SignupScreen(navController = rememberNavController())
}