package com.example.barlacteo_manuel_caceres.ui.nav
import com.example.barlacteo_manuel_caceres.ui.nav.Route

import android.net.Uri
import androidx.compose.material.icons.filled.Logout
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import androidx.compose.material3.rememberDrawerState
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.barlacteo_manuel_caceres.R
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Logout // o Close
import androidx.compose.material3.TopAppBarDefaults.pinnedScrollBehavior
import kotlinx.coroutines.launch

/* Rutas tipadas */


/* NAV HOST */
@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Route.Home.path) {
        composable(Route.Home.path) {
            HomeScreen { nombre, fono ->
                nav.navigate(Route.Siguiente.build(nombre, fono))
            }
        }
        composable(
            route = Route.Siguiente.path,
            arguments = listOf(
                navArgument("nombre") { type = NavType.StringType; defaultValue = "" },
                navArgument("fono")   { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStack ->
            val nombre = backStack.arguments?.getString("nombre").orEmpty()
            val fono   = backStack.arguments?.getString("fono").orEmpty()
            SiguienteScreen(
                nombre = nombre,
                fono = fono,
                onLogout = {
                    // Regresar a Home sin cerrar la app
                    nav.popBackStack(Route.Home.path, inclusive = false)
                    // Si quisieras limpiar todo el backstack:
                    // nav.navigate(Route.Home.path) { popUpTo(0) }
                }
            )
        }
    }
}

/* HOME + FORM */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onContinue: (String, String) -> Unit) {
    val appBarState = rememberTopAppBarState()
    val scrollBehavior = exitUntilCollapsedScrollBehavior(appBarState)
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
            FormRegistro(
                modifier = Modifier.fillMaxWidth(),
                onContinue = onContinue
            )
        }
    }
}

@Composable
fun FormRegistro(
    modifier: Modifier = Modifier,
    onContinue: (String, String) -> Unit
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
            modifier = Modifier.fillMaxWidth(),
            enabled = nombreOk && fonoOk
        ) { Text("Continuar") }
    }
}

/* PANTALLA PRINCIPAL CON MENÚ HAMBURGUESA */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiguienteScreen(
    nombre: String,
    fono: String,
    onLogout: () -> Unit
) {
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
                    onClick = { selected = "Inicio"; scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Carrito") },
                    selected = selected == "Carrito",
                    onClick = { selected = "Carrito"; scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Ajustes") },
                    selected = selected == "Ajustes",
                    onClick = { selected = "Ajustes"; scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Salir") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    icon = { Icon(Icons.Filled.Close, contentDescription = "Salir") }
                )
            }
        }
    ) {
        val appBarState = rememberTopAppBarState()
        val scrollBehavior = pinnedScrollBehavior(appBarState)

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text(selected) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú")  // ← antes estaba Close
                        }
                    },
                    actions = { Text(fono, style = MaterialTheme.typography.labelLarge) },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { inner ->
            Box(Modifier.padding(inner).fillMaxSize()) {
                when (selected) {
                    "Inicio" -> InicioContent(nombre)
                    "Carrito" -> Placeholder("Carrito")
                    "Ajustes" -> Placeholder("Ajustes")
                }
            }
        }
    }
}

@Composable
private fun InicioContent(nombre: String) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Hola, $nombre", style = MaterialTheme.typography.headlineSmall)
        Text("Aquí irá tu catálogo o dashboard.")
    }
}

@Composable
private fun Placeholder(titulo: String) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Text("Contenido pendiente.")
    }
}
