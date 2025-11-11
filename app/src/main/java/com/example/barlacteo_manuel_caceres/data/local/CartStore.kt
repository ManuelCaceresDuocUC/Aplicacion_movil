package com.example.barlacteo_manuel_caceres.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.cartDataStore by preferencesDataStore(name = "cart_store")

@Serializable
data class CartItem(
    val productId: String,
    val name: String,
    val priceCents: Long,
    val imageUrl: String? = null,
    val qty: Int
)

@Serializable
data class Cart(val items: List<CartItem> = emptyList()) {
    val count get() = items.sumOf { it.qty }
    val totalCents get() = items.sumOf { it.qty * it.priceCents }
}

class CartStore(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private fun key(userId: String) = stringPreferencesKey("cart_$userId")

    suspend fun load(userId: String): Cart {
        val raw = context.cartDataStore.data.map { it[key(userId)] }.first()
        return raw?.let { runCatching { json.decodeFromString<Cart>(it) }.getOrNull() } ?: Cart()
    }

    suspend fun save(userId: String, cart: Cart) {
        context.cartDataStore.edit { prefs ->
            prefs[key(userId)] = json.encodeToString(cart)
        }
    }
}
