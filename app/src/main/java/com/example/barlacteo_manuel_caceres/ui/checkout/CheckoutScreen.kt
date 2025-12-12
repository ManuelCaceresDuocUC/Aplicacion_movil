package com.example.barlacteo_manuel_caceres.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState   // <--- IMPORTANTE
import androidx.compose.runtime.getValue         // <--- IMPORTANTE
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.barlacteo_manuel_caceres.ui.cart.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartVm: CartViewModel,
    onBack: () -> Unit,
    userFono: String
) {
    val context = LocalContext.current
    val vm: CheckoutViewModel = viewModel()
    val cartState by cartVm.state.collectAsState()
    val total = cartState.totalCents.toInt()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Resumen de Pedido") }) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Detalle de la compra", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("${cartState.count} productos en el carro")
                Spacer(Modifier.height(16.dp))
                Text("Total a pagar:", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "$$total",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = {
                    println("BOTÓN PAGAR PRESIONADO. Enviando datos: Fono=$userFono, Total=$total")

                    vm.pagar(context, userFono, total) {
                        cartVm.clear()
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63))
            ) {
                Text("PAGAR CON WEBPAY")
            }
        }
    }
}