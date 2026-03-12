package com.example.prettypickk02.model

data class Product(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val category: String = "",
    val skinType: String = "",
    val shade: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val description: String = "",
    val imageUrl: String = ""
)
