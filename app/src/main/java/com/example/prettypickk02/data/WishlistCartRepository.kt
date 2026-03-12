package com.example.prettypickk02.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object WishlistCartRepository {

    private val db = FirebaseFirestore.getInstance()

    private val uid: String
        get() = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")


    // users/{uid}/wishlist/{productId}
    fun addToWishlist(
        productId: String,
        name: String,
        price: Double,
        imageUrl: String
    ) {
        val data = mapOf(
            "productId" to productId,
            "name" to name,
            "price" to price,
            "imageUrl" to imageUrl,
            "addedAt" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(uid)
            .collection("wishlist")
            .document(productId)
            .set(data)
            .addOnSuccessListener {
                Log.d("WISHLIST", "Added: $productId")
            }
            .addOnFailureListener {
                Log.e("WISHLIST", "Error: ${it.message}")
            }
    }

    fun removeFromWishlist(productId: String) {
        db.collection("users")
            .document(uid)
            .collection("wishlist")
            .document(productId)
            .delete()
            .addOnSuccessListener {
                Log.d("WISHLIST", "Removed: $productId")
            }
    }

    // ==========================
    // 🛒 CART / BAG
    // ==========================

    // users/{uid}/cart/{productId}
    fun addToCart(
        productId: String,
        name: String,
        price: Double,
        imageUrl: String
    ) {
        val ref = db.collection("users")
            .document(uid)
            .collection("cart")
            .document(productId)

        ref.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                // 🔁 Increase quantity
                ref.update("quantity", FieldValue.increment(1))
            } else {
                // 🆕 New cart item
                ref.set(
                    mapOf(
                        "productId" to productId,
                        "name" to name,
                        "price" to price,
                        "quantity" to 1,
                        "imageUrl" to imageUrl,
                        "addedAt" to System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // ❌ Remove item completely from cart
    fun removeFromCart(productId: String) {
        db.collection("users")
            .document(uid)
            .collection("cart")
            .document(productId)
            .delete()
            .addOnSuccessListener {
                Log.d("CART", "Removed from cart: $productId")
            }
    }

    // 🔢 Update quantity (+ / −)
    fun updateCartQuantity(
        productId: String,
        quantity: Int
    ) {
        val ref = db.collection("users")
            .document(uid)
            .collection("cart")
            .document(productId)

        if (quantity <= 0) {
            // Auto remove if quantity is 0
            removeFromCart(productId)
        } else {
            ref.update("quantity", quantity)
                .addOnSuccessListener {
                    Log.d("CART", "Quantity updated: $productId → $quantity")
                }
        }
    }
}
