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
import androidx.compose.foundation.layout.FlowRow // Asegúrate de tener compose 1.5+ o usar Accompanist si es antiguo
import androidx.compose.ui.text.style.TextOverflow
import com.example.barlacteo_manuel_caceres.ui.cart.CartViewModel

/**
 * Pantalla de catálogo conectada a Spring Boot.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class) // FlowRow puede requerir ExperimentalLayoutApi
@Composable
fun CatalogScreen(
    onBack: () -> Unit,
    cartVm: CartViewModel
) {
    val repo = remember { CatalogRepository() }

    val vm: CatalogViewModel = viewModel(
        factory = CatalogVMFactory(repo)
    )

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
            OutlinedTextField(
                value = st.query,
                onValueChange = vm::setQuery,
                label = { Text("Buscar productos") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            val activeFilter = st.categoryFilter

            val categorias = listOf("Sandwiches", "Completos", "Pizzas", "Ensaladas", "Agregados", "Bebidas")

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = activeFilter == null,
                    onClick = { vm.setCategory(null) },
                    label = { Text("Todas") }
                )

                categorias.forEach { c ->
                    FilterChip(
                        selected = activeFilter == c,
                        onClick = {
                            vm.setCategory(if (activeFilter == c) null else c)
                        },
                        label = { Text(c) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            when {
                st.loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                st.error != null -> {
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${st.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.refresh() }) { Text("Reintentar") }
                    }
                }
                else -> {

                    val items = st.items

                    if (items.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron productos")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(items) { p ->
                                ProductoCard(p = p, cartVm = cartVm)
                            }
                        }
                    }
                }
            }
        }
    }
}

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
                .error(android.R.drawable.ic_menu_report_image)
                .build(),
            contentDescription = p.title,
            modifier = Modifier.fillMaxWidth().height(120.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Column(Modifier.padding(12.dp)) {
            Text(p.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(p.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(p.category, style = MaterialTheme.typography.labelSmall)
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