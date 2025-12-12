package com.example.barlacteo_manuel_caceres.domain.model

import kotlinx.serialization.Serializable

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

data class Product(
    val id: String,
    val name: String,
    val priceCents: Long,
    val imageUrl: String? = null
)
