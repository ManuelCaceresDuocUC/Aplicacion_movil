package com.example.barlacteo_manuel_caceres.ui.catalog

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    csvUrl: String,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val vm: CatalogViewModel = viewModel(factory = CatalogVMFactory(CatalogRepository(csvUrl)))
    val st by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { inner ->
        Column(Modifier.padding(inner).padding(12.dp)) {
            // Buscador
            OutlinedTextField(
                value = st.query,
                onValueChange = vm::setQuery,
                label = { Text("Buscar productos") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Chips por categoría (dinámico)
            val cats = remember(st.items) { st.items.map { it.category }.distinct() }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = { vm.setCategory(null) },
                    label = { Text("Todas") },
                    leadingIcon = {},
                    enabled = st.categoryFilter != null
                )
                cats.forEach { c ->
                    AssistChip(
                        onClick = { vm.setCategory(c) },
                        label = { Text(c) },
                        enabled = st.categoryFilter != c
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
                    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                        Text("Error: ${st.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = vm::refresh) { Text("Reintentar") }
                    }
                }
                else -> {
                    val items = vm.filtered()
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items) { p ->
                            ProductoCard(p)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductoCard(p: Producto) {
    ElevatedCard(shape = RoundedCornerShape(16.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(p.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = p.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Column(Modifier.padding(12.dp)) {
            Text(p.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Text(p.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                AssistChip(onClick = {}, label = { Text(p.category) })
                Text(p.price, style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
