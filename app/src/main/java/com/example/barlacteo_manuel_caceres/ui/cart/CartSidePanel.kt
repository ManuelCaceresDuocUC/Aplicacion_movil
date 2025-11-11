package com.example.barlacteo_manuel_caceres.ui.cart

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.platform.LocalDensity
import com.example.barlacteo_manuel_caceres.data.local.CartItem
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CartSidePanel(
    open: Boolean,
    items: List<CartItem>,
    totalCents: Long,
    onClose: () -> Unit,
    onInc: (String) -> Unit,
    onDec: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClear: () -> Unit,
    onCheckout: () -> Unit
) {
    val widthPx = with(LocalDensity.current) { 320.dp.toPx() }.toInt()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
        AnimatedVisibility(open) {
            Box(
                Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable { onClose() }
            )
        }
        AnimatedVisibility(
            visible = open,
            enter = slideInHorizontally { widthPx } + fadeIn(),
            exit = slideOutHorizontally { widthPx } + fadeOut(),
        ) {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                modifier = Modifier.width(320.dp).fillMaxHeight()
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tu carrito", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
                    }
                    Divider()
                    LazyColumn(Modifier.weight(1f)) {
                        items(items, key = { it.productId }) { it ->
                            Row(
                                Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = it.imageUrl,
                                    contentDescription = it.name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape),          // o RoundedCornerShape(8.dp) si prefieres cuadrado
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(it.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("\$${it.priceCents / 100.0}", style = MaterialTheme.typography.labelSmall)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton({ onDec(it.productId) }) { Icon(Icons.Default.Remove, null) }
                                    Text(it.qty.toString(), modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
                                    IconButton({ onInc(it.productId) }) { Icon(Icons.Default.Add, null) }
                                    IconButton({ onRemove(it.productId) }) { Icon(Icons.Default.Delete, null) }
                                }
                            }
                        }
                    }
                    Divider()
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Total: \$${totalCents / 100.0}", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onClear,
                                modifier = Modifier.weight(1f),
                                enabled = items.isNotEmpty()
                            ) { Text("Vaciar") }
                            Button(
                                onClick = onCheckout,
                                modifier = Modifier.weight(1f),
                                enabled = items.isNotEmpty()
                            ) { Text("Finalizar") }
                        }
                    }
                }
            }
        }
    }
}
