package com.example.barlacteo_manuel_caceres.ui.screens.registro
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiguienteScreen(nombre: String, fono: String) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf("Inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Menú Principal",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = selected == "Inicio",
                    onClick = { selected = "Inicio"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Carrito") },
                    selected = selected == "Carrito",
                    onClick = { selected = "Carrito"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Ajustes") },
                    selected = selected == "Ajustes",
                    onClick = { selected = "Ajustes"; scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) }
                )
                NavigationDrawerItem(
                    label = { Text("Salir") },
                    selected = false,
                    onClick = { /* luego podrás implementar logout */ },
                    icon = { Icon(Icons.Filled.Logout, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selected) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { inner ->
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (selected) {
                    "Inicio" -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hola, $nombre", style = MaterialTheme.typography.headlineSmall)
                        Text("Tu teléfono: $fono", style = MaterialTheme.typography.bodyLarge)
                    }
                    "Carrito" -> Text("Aquí se mostrará el carrito de compras")
                    "Ajustes" -> Text("Configuraciones y preferencias")
                }
            }
        }
    }
}
