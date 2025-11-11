package com.example.barlacteo_manuel_caceres.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.local.CartItem
import com.example.barlacteo_manuel_caceres.data.repository.CartRepository
import com.example.barlacteo_manuel_caceres.domain.model.Producto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val count: Int = 0,
    val totalCents: Long = 0,
    val isPanelOpen: Boolean = false
)

class CartViewModel(
    private val repo: CartRepository,
    private val userId: String
) : ViewModel() {

    private val panel = MutableStateFlow(false)

    val state: StateFlow<CartUiState> =
        combine(repo.observe(userId), panel) { cart, open ->
            CartUiState(cart.items, cart.count, cart.totalCents, open)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, CartUiState())

    init { viewModelScope.launch { repo.load(userId) } }

    fun togglePanel() = panel.update { !it }
    fun openPanel() = panel.update { true }
    fun closePanel() = panel.update { false }

    // API con tu Producto
    fun add(p: Producto, qty: Int = 1) = viewModelScope.launch { repo.add(userId, p, qty) }
    fun inc(id: String) = viewModelScope.launch { repo.changeQty(userId, id, +1) }
    fun dec(id: String) = viewModelScope.launch { repo.changeQty(userId, id, -1) }
    fun remove(id: String) = viewModelScope.launch { repo.remove(userId, id) }
    fun clear() = viewModelScope.launch { repo.clear(userId) }
}
