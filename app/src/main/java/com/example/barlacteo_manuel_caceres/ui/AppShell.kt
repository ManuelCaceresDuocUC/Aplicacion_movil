package com.example.barlacteo_manuel_caceres.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.barlacteo_manuel_caceres.data.local.CartStore
import com.example.barlacteo_manuel_caceres.data.repository.CartRepository
import com.example.barlacteo_manuel_caceres.ui.cart.CartSidePanel
import com.example.barlacteo_manuel_caceres.ui.cart.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.compose.ui.Modifier
@Composable
fun AppShell(
    userId: String,
    context: android.content.Context,
    content: @Composable (CartViewModel) -> Unit
) {
    val vm = remember(userId) {
        val repo = CartRepository(CartStore(context), CoroutineScope(SupervisorJob() + Dispatchers.IO))
        CartViewModel(repo, userId)
    }
    val st by vm.state.collectAsState()

    Box {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { vm.togglePanel() }) {
                    BadgedBox(badge = { if (st.count > 0) Badge { Text(st.count.toString()) } }) {
                        Icon(Icons.Default.ShoppingCart, null)
                    }
                }
            }
        ) { inner -> Box(Modifier.padding(inner)) { content(vm) } }

        CartSidePanel(
            open = st.isPanelOpen,
            items = st.items,
            totalCents = st.totalCents,
            onClose = vm::closePanel,
            onInc = vm::inc,
            onDec = vm::dec,
            onRemove = vm::remove,
            onClear = vm::clear,
            onCheckout = { /* TODO */ }
        )
    }
}
