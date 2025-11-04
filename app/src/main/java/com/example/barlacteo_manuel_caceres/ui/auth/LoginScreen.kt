package com.example.barlacteo_manuel_caceres.ui.auth

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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginOk: (nombre: String, fono: String) -> Unit,
    onGoRegister: () -> Unit
) {
    val ctx = LocalContext.current
    val vm: AuthViewModel = viewModel(factory = AuthVMFactory(AccountRepository(ctx)))
    val st by vm.state.collectAsState()
    val fonoOk = st.fono.matches(Regex("^\\+569\\d{8}$"))
    val nombreOk = st.nombre.isNotBlank()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Inicio de sesión") }) }
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = st.nombre,
                onValueChange = vm::updateNombre,
                label = { Text("Nombre") },
                isError = st.errorNombre != null,
                supportingText = { st.errorNombre?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = st.fono,
                onValueChange = vm::updateFono,
                label = { Text("Celular (+569########)") },
                isError = st.errorFono != null,
                supportingText = { st.errorFono?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {})
            )

// Animación de error general
            AnimatedVisibility(visible = st.error != null) {
                Text(st.error ?: "", color = MaterialTheme.colorScheme.error)
            }

// Botón con Crossfade
            Button(
                onClick = { vm.login { onLoginOk(st.nombre.trim(), st.fono.trim()) } },
                enabled = st.canSubmit && !st.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(targetState = st.loading) { loading ->
                    Text(if (loading) "Ingresando..." else "Iniciar sesión")
                }
            }

            TextButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                Text("¿No tienes cuenta? Crear una")
            }
        }
    }
}