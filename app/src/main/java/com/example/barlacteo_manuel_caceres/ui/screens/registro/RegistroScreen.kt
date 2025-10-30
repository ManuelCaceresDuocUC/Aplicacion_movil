package com.example.barlacteo_manuel_caceres.ui.screens.registro

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.barlacteo_manuel_caceres.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onContinue: (String, String) -> Unit) {   // <-- agrega el callback aquí
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)
    val focus = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Bienvenido a Bar lácteo Apolinav") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) { detectTapGestures { focus.clearFocus() } }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_bartolo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            FormRegistro(                                     // <-- pásalo al form
                modifier = Modifier.fillMaxWidth(),
                onContinue = onContinue
            )
        }
    }
}

@Composable
fun FormRegistro(
    modifier: Modifier = Modifier,
    onContinue: (String, String) -> Unit                    // <-- define el callback aquí
) {
    val focus = LocalFocusManager.current
    var nombre by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }

    val nombreOk = nombre.isNotBlank()
    val fonoOk = telefono.matches(Regex("^\\+569\\d{8}$"))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("+569xxxxxxxx") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = telefono.isNotEmpty() && !fonoOk,
            supportingText = {
                if (telefono.isNotEmpty() && !fonoOk) Text("Formato: +569########")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focus.clearFocus() })
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = { if (nombreOk && fonoOk) onContinue(nombre.trim(), telefono.trim()) },
            enabled = nombreOk && fonoOk,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continuar") }
    }
}
