package com.example.barlacteo_manuel_caceres.ui.principal

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

/**
 * Pantalla principal con app bar colapsable y formulario de registro.
 * @param onContinue callback que recibe (nombre, teléfono) cuando el form es válido.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onContinue: (String, String) -> Unit) {
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
                // Cierra foco/teclado cuando el usuario toca el fondo.
                .pointerInput(Unit) { detectTapGestures { focus.clearFocus() } }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_bartolo),
                contentDescription = null, // Opcional: añade texto para accesibilidad.
                modifier = Modifier.size(120.dp)
            )

            FormRegistro(
                modifier = Modifier.fillMaxWidth(),
                onContinue = onContinue
            )
        }
    }
}

/**
 * Formulario de registro básico: nombre y teléfono chileno.
 * Valida formato: +569######## y habilita el botón Continuar.
 */
@Composable
fun FormRegistro(
    modifier: Modifier = Modifier,
    onContinue: (String, String) -> Unit
) {
    val focus = LocalFocusManager.current

    // Estado persistente ante recomposición y rotación.
    var nombre by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }

    // Reglas de validación simples.
    val nombreOk = nombre.isNotBlank()
    val fonoOk = telefono.matches(Regex("^\\+569\\d{8}$"))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Campo: Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Campo: Teléfono. Muestra error si no respeta el patrón +569########
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

        // Acción principal. Solo habilitada si ambas validaciones pasan.
        Button(
            onClick = { if (nombreOk && fonoOk) onContinue(nombre.trim(), telefono.trim()) },
            enabled = nombreOk && fonoOk,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Continuar") }
    }
}
