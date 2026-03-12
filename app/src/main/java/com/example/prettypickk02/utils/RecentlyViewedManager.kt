package com.example.prettypickk02.utils

import android.content.Context
import com.example.prettypickk02.model.Product
import com.google.gson.Gson
import com.google.common.reflect.TypeToken

object RecentlyViewedManager {

    private const val PREF_NAME = "recent_viewed_products"
    private const val KEY = "data"

    fun saveProduct(context: Context, product: Product) {

        val prefs =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val gson = Gson()

        val existing = getProducts(context).toMutableList()

        val updated =
            listOf(product) + existing.filter { it.id != product.id }.take(9)

        prefs.edit()
            .putString(KEY, gson.toJson(updated))
            .apply()
    }

    fun getProducts(context: Context): List<Product> {

        val prefs =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = prefs.getString(KEY, null) ?: return emptyList()

        val type =
            object : TypeToken<List<Product>>() {}.type

        return Gson().fromJson(json, type)
    }
}
