package com.example.barlacteo_manuel_caceres.ui.catalog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.barlacteo_manuel_caceres.data.repository.CatalogRepository
import com.example.barlacteo_manuel_caceres.domain.model.Producto
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.text.style.TextOverflow
import com.example.barlacteo_manuel_caceres.ui.cart.CartViewModel

/**
 * Pantalla de catálogo con:
 * - Búsqueda por texto
 * - Filtro por categoría
 * - Grid adaptativo de productos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    csvUrl: String,
    onBack: () -> Unit,
    cartVm: CartViewModel               // ← NUEVO

) {
    val ctx = LocalContext.current

    // VM con repo que consume el CSV remoto
    val vm: CatalogViewModel =
        viewModel(factory = CatalogVMFactory(CatalogRepository(csvUrl)))

    // Estado observable de la pantalla
    val st by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(12.dp)
        ) {
            // ===== Buscador =====
            OutlinedTextField(
                value = st.query,
                onValueChange = vm::setQuery,                 // delega cambio al VM
                label = { Text("Buscar productos") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // ===== Chips de categoría (dinámicos) =====
            // Distinct de categorías a partir de los items cargados.
            val cats = remember(st.items) { st.items.map { it.category }.distinct() }

            // FlowRow permite saltar de línea de forma fluida.
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Chip "Todas" = sin filtro
                AssistChip(
                    onClick = { vm.setCategory(null) },
                    label = { Text("Todas") },
                    // enabled solo si hay un filtro activo; si ya está en "Todas", se deshabilita
                    enabled = st.categoryFilter != null
                )
                cats.forEach { c ->
                    AssistChip(
                        onClick = { vm.setCategory(c) },
                        label = { Text(c) },
                        // deshabilita el chip cuando ya está seleccionado
                        enabled = st.categoryFilter != c
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ===== Contenido según estado =====
            when {
                st.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                st.error != null -> {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                        Text("Error: ${st.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = vm::refresh) { Text("Reintentar") }
                    }
                }
                else -> {
                    // Lista ya filtrada en memoria por VM
                    val items = vm.filtered()

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items) { p ->
                            ProductoCard(p = p, cartVm = cartVm)   // ← pasar VM
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta de producto con imagen, título, descripción corta, categoría y precio.
 */
@Composable
fun ProductoCard(
    p: Producto,
    cartVm: CartViewModel
) {
    ElevatedCard(shape = RoundedCornerShape(16.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(p.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = p.title,
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        Column(Modifier.padding(12.dp)) {
            Text(p.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(p.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AssistChip(onClick = {}, label = { Text(p.category) })
                Text(p.price, style = MaterialTheme.typography.titleSmall)
            }

            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    cartVm.add(p)
                    cartVm.openPanel()
                }) {
                    Icon(Icons.Filled.AddShoppingCart, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agregar")
                }
            }
        }
    }
}
