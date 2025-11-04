package com.example.barlacteo_manuel_caceres.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository

/**
 * Pantalla de registro.
 * - Valida nombre y teléfono con feedback inline.
 * - Llama a register() en el VM y notifica a la Nav con onRegistered.
 * - No dibuja AppBar local; AppNav ya maneja la barra por ruta.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistered: (nombre: String, fono: String) -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    // VM con repo inyectado manualmente (sin Hilt).
    val vm: AuthViewModel = viewModel(factory = AuthVMFactory(AccountRepository(ctx)))

    // Estado observable del formulario: campos, errores y flags.
    val st by vm.state.collectAsState()

    Scaffold(
        topBar = {}, // Evita doble AppBar
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Back dentro del contenido. La AppBar global ya puede tener back si quisieras.
            TextButton(onClick = onBack) { Text("Atrás") }

            // ===== Campo: Nombre =====
            OutlinedTextField(
                value = st.nombre,
                onValueChange = vm::updateNombre,          // validación en VM
                label = { Text("Nombre") },
                isError = st.errorNombre != null,
                supportingText = { st.errorNombre?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // ===== Campo: Teléfono =====
            OutlinedTextField(
                value = st.fono,
                onValueChange = vm::updateFono,            // validación en VM
                label = { Text("Celular (+569########)") },
                isError = st.errorFono != null,
                supportingText = { st.errorFono?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { /* opcional: vm.submit() */ })
            )

            // Error general (por ejemplo, fallo al guardar).
            AnimatedVisibility(visible = st.error != null) {
                Text(st.error ?: "", color = MaterialTheme.colorScheme.error)
            }

            // ===== Acción principal =====
            Button(
                onClick = {
                    // register ejecuta lógica asíncrona y, si va bien, invoca el callback provisto.
                    vm.register {
                        onRegistered(st.nombre.trim(), st.fono.trim())
                    }
                },
                enabled = st.canSubmit && !st.loading,     // evita spam y submit inválido
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(targetState = st.loading) { loading ->
                    Text(if (loading) "Guardando..." else "Crear cuenta")
                }
            }
        }
    }
}
