package com.example.prettypickk02.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

// ─────────────────────────────────────────────
//  PASTEL PINK DESIGN TOKENS
// ─────────────────────────────────────────────
private val AppBg           = Color(0xFFFFF5F8)
private val AppBgAlt        = Color(0xFFFFF0F4)
private val CardWhite       = Color(0xFFFFFFFF)
private val PinkPrimary     = Color(0xFFE91E8C)
private val PinkMedium      = Color(0xFFF48FB1)
private val PinkSoft        = Color(0xFFFCE4EC)
private val PinkBorder      = Color(0xFFF8BBD9)
private val PurpleAccent    = Color(0xFFCE93D8)
private val GoldAccent      = Color(0xFFFFB74D)
private val TextPrimary     = Color(0xFF2D1B26)
private val TextSecondary   = Color(0xFF7B4F63)
private val TextMuted02      = Color(0xFFBB8FA6)   //
private val DividerColor    = Color(0xFFF5D0E0)
private val SuccessGreen    = Color(0xFF43A047)
private val ErrorRed        = Color(0xFFE53935)
private val ChipUnselected  = Color(0xFFFDE8F0)
private val ChipBorderUnsel = Color(0xFFF8BBD9)

// ─────────────────────────────────────────────
//  MAIN SCREEN
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddProductScreen() {

    val context    = LocalContext.current
    val db         = FirebaseFirestore.getInstance()
    val storageRef = FirebaseStorage.getInstance().reference
    val auth       = FirebaseAuth.getInstance()

    // ── Core fields ──
    var name        by remember { mutableStateOf("") }
    var brand       by remember { mutableStateOf("") }
    var category    by remember { mutableStateOf("Makeup") }
    var subcategory by remember { mutableStateOf("") }
    var shade       by remember { mutableStateOf("") }
    var skinType    by remember { mutableStateOf("All") }
    var price       by remember { mutableStateOf("") }
    var stock       by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // ── Extra fields ──
    var originalPrice   by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }
    var isOnSale        by remember { mutableStateOf(false) }
    var isNewArrival    by remember { mutableStateOf(false) }
    var isBestSeller    by remember { mutableStateOf(false) }
    var isFeatured      by remember { mutableStateOf(false) }
    var subSubcategory  by remember { mutableStateOf("") }
    var gender          by remember { mutableStateOf("Women") }
    var sizeVolume      by remember { mutableStateOf("") }

    // ── Single image ──
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(false) }
    var error     by remember { mutableStateOf("") }
    var success   by remember { mutableStateOf(false) }

    // ── Auto-computed discounted price ──
    val computedDiscountedPrice = remember(originalPrice, discountPercent) {
        val op = originalPrice.toDoubleOrNull()
        val dp = discountPercent.toDoubleOrNull()
        if (op != null && dp != null && dp in 0.0..100.0)
            "₹${"%.0f".format(op - (op * dp / 100))}"
        else ""
    }

    // ── Brand list ──
    val brandList = listOf(
        "Swiss Beauty","Mars","Kay Beauty","Lakme","Maybelline",
        "L'Oréal Paris","MAC","Huda Beauty","NYX","Colorbar",
        "Mamaearth","Plum","Sugar Cosmetics","Faces Canada","Insight",
        "Elle 18","Revlon","Rare Beauty","Fenty Beauty","Charlotte Tilbury",
        "The Body Shop","Clinique","Estee Lauder","Dot & Key","Minimalist",
        "The Derma Co","Nivea","Engage","Dove","Nykaa","Be BodyWise",
        "VWash","Sanfe","Vaseline","ChemistPlay","BellaVita"
    )
    var brandExpanded by remember { mutableStateOf(false) }

    // ── Category → Subcategory map ──
    val subCategoryMap = mapOf(
        "Makeup"      to listOf("Face","Eyes","Lips","Tools & Brushes","Makeup Palettes","Makeup Kits"),
        "Skin"        to listOf("Face Wash","Cleansers","Serums","Moisturizers","Sun Care","Toners & Mists","Masks","Lip Care","Eye Care","Specialised Skincare"),
        "Hair"        to listOf("Hair Care","Tools & Accessories","Hair Styling","Shop By Hair Type"),
        "Bath & Body" to listOf("Bath & Shower","Body Care","Feminine Hygiene","Female Grooming","Hands & Feet"),
        "Fragrances"  to listOf("Womens Fragrance","Mens Fragrance","Premium Fragrance")
    )

    // ── Sub-subcategory map ──
    val subSubCategoryMap = mapOf(
        "Lips"             to listOf("Lipstick","Lip Gloss","Lip Liner","Lip Balm","Lip Plumper"),
        "Face"             to listOf("Foundation","Concealer","Primer","Blush","Highlighter","Setting Powder"),
        "Eyes"             to listOf("Kajal","Eyeliner","Mascara","Eyeshadow","Brow Products"),
        "Serums"           to listOf("Vitamin C","Niacinamide","Retinol","Hyaluronic Acid","Peptide"),
        "Moisturizers"     to listOf("Day Cream","Night Cream","Gel Moisturizer","Oil-Free"),
        "Hair Care"        to listOf("Shampoo","Conditioner","Hair Mask","Hair Oil","Scalp Care"),
        "Hair Styling"     to listOf("Serum","Gel","Spray","Mousse","Wax"),
        "Bath & Shower"    to listOf("Body Wash","Soap","Scrub","Bath Salt","Shower Oil"),
        "Body Care"        to listOf("Body Lotion","Body Butter","Body Oil","Stretch Marks"),
        "Womens Fragrance" to listOf("Eau de Parfum","Eau de Toilette","Body Mist","Solid Perfume"),
        "Mens Fragrance"   to listOf("Eau de Parfum","Eau de Toilette","Deodorant","Body Spray")
    )

    // ── ✅ SINGLE image launcher ──
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { imageUri = it } }

    // ─────────────────────────────────────────
    //  UPLOAD LOGIC
    // ─────────────────────────────────────────
    fun uploadProduct() {
        val user = auth.currentUser
        if (user == null) {
            error = "You must be logged in to upload a product."
            return
        }

        // ── Price: if On Sale use computed price, else use direct price ──
        val priceValue = if (isOnSale)
            computedDiscountedPrice.removePrefix("₹").toDoubleOrNull()
        else
            price.toDoubleOrNull()

        val stockValue = stock.toIntOrNull()

        if (name.isBlank()) { error = "Product name is required."; return }
        if (brand.isBlank()) { error = "Brand is required."; return }
        if (subcategory.isBlank()) { error = "Please select a subcategory."; return }
        if (priceValue == null) { error = "Please enter a valid price."; return }
        if (stockValue == null) { error = "Please enter a valid stock quantity."; return }
        if (imageUri == null) { error = "Please upload a product image."; return }

        isLoading = true
        error = ""
        success = false

        val imageRef = storageRef.child("products/${user.uid}/${UUID.randomUUID()}.jpg")

        imageRef.putFile(imageUri!!)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                imageRef.downloadUrl
            }
            .addOnSuccessListener { url ->
                db.collection("products").add(
                    hashMapOf(
                        "name"            to name,
                        "brand"           to brand,
                        "category"        to category,
                        "subcategory"     to subcategory,
                        "shade"           to shade,
                        "skinType"        to skinType,
                        "price"           to priceValue,
                        "stock"           to stockValue,
                        "description"     to description,
                        "imageUrl"        to url.toString(),
                        "createdAt"       to FieldValue.serverTimestamp(),
                        "originalPrice"   to (originalPrice.toDoubleOrNull() ?: priceValue),
                        "discountPercent" to (discountPercent.toDoubleOrNull() ?: 0.0),
                        "isOnSale"        to isOnSale,
                        "isNewArrival"    to isNewArrival,
                        "isBestSeller"    to isBestSeller,
                        "isFeatured"      to isFeatured,
                        "subSubcategory"  to subSubcategory,
                        "gender"          to gender,
                        "sizeVolume"      to sizeVolume,
                        "averageRating"   to 0.0,
                        "totalRatings"    to 0,
                        "uploadedBy"      to user.uid
                    )
                ).addOnSuccessListener {
                    isLoading = false
                    success = true
                    Toast.makeText(context, "✅ Product published!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    isLoading = false
                    error = "Firestore error: ${e.message}"
                }
            }
            .addOnFailureListener { e ->
                isLoading = false
                error = "Storage error: ${e.message}"
            }
    }

    // ─────────────────────────────────────────
    //  ROOT UI
    // ─────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 20.dp, bottom = 100.dp)
        ) {

            PastelHeader()
            Spacer(Modifier.height(22.dp))

            // ── 1. Image ──
            PastelSectionCard("Product Image", Icons.Outlined.PhotoCamera, PinkPrimary) {
                SingleImageUpload(
                    imageUri = imageUri,
                    onAdd    = { launcher.launch("image/*") },
                    onRemove = { imageUri = null }
                )
            }
            Spacer(Modifier.height(14.dp))

            // ── 2. Product Details ──
            PastelSectionCard("Product Details", Icons.Outlined.Info, PinkMedium) {

                // ✅ Product Name — always visible
                PastelTextField(name, { name = it }, "Product Name *", Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))

                PastelTextField(shade, { shade = it }, "Shade / Colour (Optional)", Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))

                PastelTextField(sizeVolume, { sizeVolume = it }, "Size / Volume (e.g. 30ml, 5g)", Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))

                val descLen = description.length
                PastelTextField(
                    description, { if (it.length <= 500) description = it },
                    "Description *", Modifier.fillMaxWidth(), minLines = 4
                )
                Text(
                    "$descLen / 500",
                    color    = if (descLen > 450) PinkPrimary else TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(14.dp))

            // ── 3. Brand ──
            PastelSectionCard("Brand", Icons.Outlined.Storefront, PurpleAccent) {
                ExposedDropdownMenuBox(
                    expanded = brandExpanded,
                    onExpandedChange = { brandExpanded = !brandExpanded }
                ) {
                    PastelTextField(
                        value = brand,
                        onValueChange = { brand = it; brandExpanded = true },
                        label = "Brand Name *",
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) }
                    )
                    val filtered = brandList.filter { it.contains(brand, ignoreCase = true) }
                    ExposedDropdownMenu(
                        expanded = brandExpanded && filtered.isNotEmpty(),
                        onDismissRequest = { brandExpanded = false },
                        modifier = Modifier.background(CardWhite)
                    ) {
                        filtered.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b, color = TextPrimary) },
                                onClick = { brand = b; brandExpanded = false }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            // ── 4. Pricing — always fully visible ──
            PastelSectionCard("Pricing & Discount", Icons.Outlined.Sell, PinkPrimary) {

                // ✅ Direct selling price — always visible
                PastelTextField(price, { price = it }, "Selling Price ₹ *", Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))

                PastelTextField(stock, { stock = it }, "Stock Quantity *", Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))

                HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
                Spacer(Modifier.height(14.dp))

                // ✅ On Sale toggle — always visible
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(if (isOnSale) PinkSoft else AppBgAlt)
                        .border(1.dp, if (isOnSale) PinkPrimary.copy(0.5f) else DividerColor, RoundedCornerShape(13.dp))
                        .clickable { isOnSale = !isOnSale }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "🔥  On Sale",
                            color = if (isOnSale) TextPrimary else TextSecondary,
                            fontWeight = if (isOnSale) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                        Text(
                            "Enable to set original price + discount %",
                            color = TextMuted, fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isOnSale,
                        onCheckedChange = { isOnSale = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White,
                            checkedTrackColor   = PinkPrimary,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DividerColor
                        )
                    )
                }

                // ✅ Discount fields — only visible when isOnSale is ON
                AnimatedVisibility(visible = isOnSale) {
                    Column {
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PastelTextField(
                                originalPrice, { originalPrice = it },
                                "Original Price ₹ *",
                                Modifier.weight(1f)
                            )
                            PastelTextField(
                                discountPercent, { discountPercent = it },
                                "Discount %",
                                Modifier.weight(1f)
                            )
                        }
                        // ✅ Auto-computed final price preview
                        AnimatedVisibility(visible = computedDiscountedPrice.isNotEmpty()) {
                            Column {
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .border(1.dp, SuccessGreen.copy(0.3f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Discounted Price: ", color = TextSecondary, fontSize = 13.sp)
                                    Text(
                                        computedDiscountedPrice,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(14.dp))

            // ── 5. Badges ──
            PastelSectionCard("Product Badges", Icons.Outlined.LocalOffer, GoldAccent) {
                PastelBadgeSection(
                    isNewArrival, { isNewArrival = it },
                    isBestSeller, { isBestSeller = it },
                    isFeatured, { isFeatured = it }
                )
            }
            Spacer(Modifier.height(14.dp))

            // ── 6. Category ──
            PastelSectionCard("Category", Icons.Outlined.Category, PurpleAccent) {
                PastelCategorySection(
                    category, subcategory, subSubcategory,
                    subCategoryMap, subSubCategoryMap,
                    { category = it; subcategory = ""; subSubcategory = "" },
                    { subcategory = it; subSubcategory = "" },
                    { subSubcategory = it }
                )
            }
            Spacer(Modifier.height(14.dp))

            // ── 7. Target & Skin Type ──
            PastelSectionCard("Target & Skin Type", Icons.Outlined.People, GoldAccent) {
                PastelLabel("Gender")
                Spacer(Modifier.height(8.dp))
                PastelPillRow(listOf("Women", "Men", "Unisex"), gender) { gender = it }
                Spacer(Modifier.height(14.dp))
                PastelLabel("Skin Type")
                Spacer(Modifier.height(8.dp))
                PastelPillRow(listOf("Dry", "Oily", "Combination", "All"), skinType) { skinType = it }
            }

            Spacer(Modifier.height(22.dp))

            // ── Error Banner ──
            AnimatedVisibility(visible = error.isNotEmpty()) {
                Column {
                    PastelErrorBanner(error)
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── Firebase Rules Hint ──
            AnimatedVisibility(
                visible = error.contains("permission", ignoreCase = true)
                        || error.contains("Storage error", ignoreCase = true)
                        || error.contains("access", ignoreCase = true)
            ) {
                Column {
                    PastelRulesHint()
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── Success Banner ──
            AnimatedVisibility(visible = success) {
                Column {
                    PastelSuccessBanner()
                    Spacer(Modifier.height(10.dp))
                }
            }

            PastelPublishButton(isLoading) { uploadProduct() }
        }
    }
}

// ─────────────────────────────────────────────
//  HEADER
// ─────────────────────────────────────────────
@Composable
fun PastelHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE91E8C), Color(0xFFCE93D8))))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AddBox, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text("Add New Product", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = (-0.3).sp)
                Text("Create a beautiful listing ✨", fontSize = 13.sp, color = Color.White.copy(alpha = 0.85f))
            }
        }
        Box(modifier = Modifier.align(Alignment.TopEnd).size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f)))
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 4.dp, end = 16.dp).size(30.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)))
    }
}

// ─────────────────────────────────────────────
//  SECTION CARD
// ─────────────────────────────────────────────
@Composable
fun PastelSectionCard(
    title: String,
    icon: ImageVector,
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), ambientColor = PinkSoft, spotColor = PinkBorder)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(11.dp)).background(accent.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accent, modifier = Modifier.size(19.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = DividerColor, thickness = 0.8.dp)
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

// ─────────────────────────────────────────────
//  ✅ SINGLE IMAGE UPLOAD
// ─────────────────────────────────────────────
@Composable
fun SingleImageUpload(imageUri: Uri?, onAdd: () -> Unit, onRemove: () -> Unit) {
    if (imageUri == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(PinkPrimary.copy(0.5f), PurpleAccent.copy(0.5f))),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(PinkSoft)
                .clickable { onAdd() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.AddPhotoAlternate, null, tint = PinkPrimary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(10.dp))
                Text("Tap to upload product image", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("JPG, PNG supported", color = TextMuted, fontSize = 12.sp)
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, PinkPrimary, RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // ✅ Remove / Change button
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Change image
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(0.92f))
                        .clickable { onAdd() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Change", color = PinkPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                // Remove image
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.92f))
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                }
            }
            // Main badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PinkPrimary)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Product Image", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  BADGE TOGGLES (On Sale removed — now in Pricing)
// ─────────────────────────────────────────────
@Composable
fun PastelBadgeSection(
    isNewArrival: Boolean, onNewArrivalChange: (Boolean) -> Unit,
    isBestSeller: Boolean, onBestSellerChange: (Boolean) -> Unit,
    isFeatured: Boolean,   onFeaturedChange: (Boolean) -> Unit
) {
    val badges = listOf(
        Triple("✨  New Arrival",  isNewArrival, onNewArrivalChange),
        Triple("⭐  Best Seller",  isBestSeller, onBestSellerChange),
        Triple("💎  Featured",    isFeatured,   onFeaturedChange)
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        badges.forEach { (label, checked, onChange) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(if (checked) PinkSoft else AppBgAlt)
                    .border(1.dp, if (checked) PinkPrimary.copy(0.5f) else DividerColor, RoundedCornerShape(13.dp))
                    .clickable { onChange(!checked) }
                    .padding(horizontal = 16.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    label,
                    color = if (checked) TextPrimary else TextSecondary,
                    fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                )
                Switch(
                    checked = checked, onCheckedChange = onChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor   = Color.White,
                        checkedTrackColor   = PinkPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DividerColor
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  CATEGORY SECTION
// ─────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PastelCategorySection(
    category: String, subcategory: String, subSubcategory: String,
    subCategoryMap: Map<String, List<String>>,
    subSubCategoryMap: Map<String, List<String>>,
    onCategoryChange: (String) -> Unit,
    onSubcategoryChange: (String) -> Unit,
    onSubSubcategoryChange: (String) -> Unit
) {
    PastelLabel("Category *")
    Spacer(Modifier.height(8.dp))
    PastelPillRow(listOf("Makeup","Skin","Hair","Bath & Body","Fragrances"), category, onCategoryChange)

    Spacer(Modifier.height(14.dp))
    PastelLabel("Subcategory *")
    Spacer(Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        subCategoryMap[category]?.forEach { sub ->
            PastelPillChip(sub, subcategory == sub) { onSubcategoryChange(sub) }
        }
    }

    val subSubs = subSubCategoryMap[subcategory]
    AnimatedVisibility(visible = !subSubs.isNullOrEmpty()) {
        Column {
            Spacer(Modifier.height(14.dp))
            PastelLabel("Specific Type")
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                subSubs?.forEach { ss ->
                    PastelPillChip(ss, subSubcategory == ss) { onSubSubcategoryChange(ss) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  REUSABLE UI ATOMS
// ─────────────────────────────────────────────
@Composable
fun PastelLabel(text: String) {
    Text(text, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PastelPillRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { PastelPillChip(it, selected == it) { onSelect(it) } }
    }
}

@Composable
fun PastelPillChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) PinkPrimary else ChipUnselected)
            .border(1.dp, if (selected) PinkPrimary else ChipBorderUnsel, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            color      = if (selected) Color.White else TextSecondary,
            fontSize   = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastelTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label, fontSize = 13.sp) },
        modifier       = modifier,
        minLines       = minLines,
        trailingIcon   = trailingIcon,
        shape          = RoundedCornerShape(14.dp),
        colors         = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = TextPrimary,
            unfocusedTextColor      = TextPrimary,
            focusedBorderColor      = PinkPrimary,
            unfocusedBorderColor    = PinkBorder,
            cursorColor             = PinkPrimary,
            focusedLabelColor       = PinkPrimary,
            unfocusedLabelColor     = TextMuted,
            focusedContainerColor   = CardWhite,
            unfocusedContainerColor = AppBg
        )
    )
}

// ─────────────────────────────────────────────
//  BANNERS
// ─────────────────────────────────────────────
@Composable
fun PastelErrorBanner(message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFEBEE))
            .border(1.dp, ErrorRed.copy(0.3f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(message, color = ErrorRed, fontSize = 13.sp)
    }
}

@Composable
fun PastelSuccessBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8F5E9))
            .border(1.dp, SuccessGreen.copy(0.3f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text("Product published successfully! 🎉", color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PastelRulesHint() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFFFF8E1))
            .border(1.dp, GoldAccent.copy(0.5f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, null, tint = GoldAccent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Fix Firebase Permissions", color = Color(0xFF6D4C00), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text("1. Firebase Console → Storage → Rules", color = Color(0xFF7B5800), fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF3CD)).padding(10.dp)
        ) {
            Text("allow read, write: if request.auth != null;", color = Color(0xFF5D4037), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(6.dp))
        Text("2. Same for Firestore → Rules → products collection.", color = Color(0xFF7B5800), fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text("3. Ensure FirebaseAuth.currentUser is not null.", color = Color(0xFF7B5800), fontSize = 12.sp)
    }
}

// ─────────────────────────────────────────────
//  PUBLISH BUTTON
// ─────────────────────────────────────────────
@Composable
fun PastelPublishButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick        = onClick,
        enabled        = !isLoading,
        modifier       = Modifier.fillMaxWidth().height(56.dp),
        shape          = RoundedCornerShape(18.dp),
        colors         = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isLoading) Modifier.background(DividerColor)
                    else Modifier.background(Brush.linearGradient(listOf(Color(0xFFE91E8C), Color(0xFFAD1457))))
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = PinkPrimary, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Publishing...", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RocketLaunch, null, tint = Color.White, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Publish Product", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 0.3.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  PREVIEW
// ─────────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFFFFF5F8)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen()
}