package com.example.barlacteo_manuel_caceres.ui.screens.auth

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
import com.example.barlacteo_manuel_caceres.data.AccountRepository
import com.example.barlacteo_manuel_caceres.ui.auth.AuthVMFactory
import com.example.barlacteo_manuel_caceres.ui.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistered: (nombre: String, fono: String) -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val vm: AuthViewModel = viewModel(factory = AuthVMFactory(AccountRepository(ctx)))
    val st by vm.state.collectAsState()
    val fonoOk = st.fono.matches(Regex("^\\+569\\d{8}$"))
    val nombreOk = st.nombre.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier.padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = st.nombre, onValueChange = vm::updateNombre,
                label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            OutlinedTextField(
                value = st.fono, onValueChange = vm::updateFono,
                label = { Text("Celular (+569########)") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                isError = st.fono.isNotEmpty() && !fonoOk,
                supportingText = {
                    if (st.fono.isNotEmpty() && !fonoOk) Text("Formato requerido: +569########")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {})
            )

            if (st.error != null) {
                Text(st.error ?: "", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { vm.register { onRegistered(st.nombre.trim(), st.fono.trim()) } },
                enabled = nombreOk && fonoOk && !st.loading,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (st.loading) "Guardando..." else "Crear cuenta") }
        }
    }
}
