package com.example.barlacteo_manuel_caceres.data.repository

import com.example.barlacteo_manuel_caceres.data.local.Cart
import com.example.barlacteo_manuel_caceres.data.local.CartItem
import com.example.barlacteo_manuel_caceres.data.local.CartStore
import com.example.barlacteo_manuel_caceres.domain.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartRepository(
    private val store: CartStore,
    private val scope: CoroutineScope
) {
    private val carts = MutableStateFlow<Map<String, Cart>>(emptyMap())

    fun observe(userId: String): StateFlow<Cart> =
        carts.map { it[userId] ?: Cart() }
            .stateIn(scope, SharingStarted.Eagerly, Cart())

    suspend fun load(userId: String) {
        val cart = store.load(userId)
        carts.update { it + (userId to cart) }
    }

    private suspend fun persist(userId: String, cart: Cart) {
        store.save(userId, cart)
        carts.update { it + (userId to cart) }
    }

    // === API usando tu Producto ===
    suspend fun add(userId: String, p: Producto, qty: Int = 1) {
        val current = carts.value[userId] ?: Cart()
        val pid = p.stableId()
        val priceCents = p.price.toClpCents()
        val items = current.items.toMutableList()
        val i = items.indexOfFirst { it.productId == pid }
        if (i >= 0) items[i] = items[i].copy(qty = items[i].qty + qty)
        else items += CartItem(pid, p.title, priceCents, p.imageUrl, qty)
        persist(userId, Cart(items))
    }

    suspend fun changeQty(userId: String, productId: String, delta: Int) {
        val current = carts.value[userId] ?: Cart()
        val items = current.items.mapNotNull {
            if (it.productId != productId) it
            else {
                val q = (it.qty + delta).coerceAtLeast(0)
                if (q == 0) null else it.copy(qty = q)
            }
        }
        persist(userId, Cart(items))
    }

    suspend fun remove(userId: String, productId: String) {
        val current = carts.value[userId] ?: Cart()
        persist(userId, Cart(current.items.filterNot { it.productId == productId }))
    }

    suspend fun clear(userId: String) {
        persist(userId, Cart())
    }
}
