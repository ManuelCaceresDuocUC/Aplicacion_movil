package com.example.barlacteo_manuel_caceres.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.barlacteo_manuel_caceres.ui.catalog.CatalogScreen

import kotlinx.coroutines.launch
import com.example.barlacteo_manuel_caceres.domain.model.Oferta
import com.example.barlacteo_manuel_caceres.ui.auth.LoginScreen
import com.example.barlacteo_manuel_caceres.ui.auth.RegisterScreen
import androidx.compose.ui.platform.LocalContext
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository
import com.example.barlacteo_manuel_caceres.ui.principal.SiguienteScreen

/**
 * Gráfico de navegación principal con Drawer.
 * Rutas: Login, Register, Siguiente(nombre,fono), Perfil, Catálogo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(modifier: Modifier = Modifier) {
    // Controlador de navegación.
    val nav = rememberNavController()

    // Alcance para abrir/cerrar drawer sin bloquear UI.
    val scope = rememberCoroutineScope()

    // Estado del Drawer.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Ruta actual para marcar selección y condicionar UI.
    val backEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route.orEmpty()

    // En pantallas de auth se deshabilita Drawer y el ícono hamburguesa.
    val isLoginOrRegister = currentRoute == Route.Login.path || currentRoute == Route.Register.path

    // Cuenta actual para precargar nombre/fono en “Inicio”.
    val ctx = LocalContext.current
    val accountRepo = remember { AccountRepository(ctx) }
    val currentAccount by remember { accountRepo.currentAccountFlow }.collectAsState(initial = null)

    // ----- Drawer + Scaffold -----
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isLoginOrRegister, // bloquea gesto en login/register
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "BarLácteo",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                // Item: Inicio -> navega a Siguiente con nombre/fono
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = currentRoute.startsWith("siguiente"),
                    onClick = {
                        val nombre = currentAccount?.nombre ?: "Invitado"
                        val fono = currentAccount?.fono ?: "+56912345678"
                        nav.navigate(Route.Siguiente.to(nombre, fono)) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                // Item: Perfil
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = currentRoute == Route.Perfil.path,
                    onClick = {
                        nav.navigate(Route.Perfil.path) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                // Item: Catálogo
                NavigationDrawerItem(
                    label = { Text("Catálogo") },
                    selected = currentRoute == Route.Catalog.path,
                    onClick = {
                        nav.navigate(Route.Catalog.path) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )

                // Item: Cerrar sesión
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            // 1) limpiar sesión
                            accountRepo.logout()
                            // 2) cerrar drawer antes de navegar
                            drawerState.close()
                            // 3) navegar a Login limpiando back stack
                            nav.navigate(Route.Login.path) {
                                popUpTo(nav.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when {
                                currentRoute == Route.Login.path -> "Inicio de sesión"
                                currentRoute == Route.Register.path -> "Crear cuenta"
                                else -> "BarLácteo"
                            }
                        )
                    },
                    navigationIcon = {
                        if (!isLoginOrRegister) {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                            }
                        }
                    }
                )
            }
        ) { inner ->
            // ----- Host de navegación -----
            NavHost(
                navController = nav,
                startDestination = Route.Login.path, // flujo arranca en Login
                modifier = modifier.padding(inner)
            ) {
                // Pantalla: Login
                composable(Route.Login.path) {
                    LoginScreen(
                        onLoginOk = { nombre, fono ->
                            nav.navigate(Route.Siguiente.to(nombre, fono)) {
                                popUpTo(Route.Login.path) { inclusive = true } // limpia auth del stack
                            }
                        },
                        onGoRegister = { nav.navigate(Route.Register.path) }
                    )
                }

                // Pantalla: Registro
                composable(Route.Register.path) {
                    RegisterScreen(
                        onRegistered = { nombre, fono ->
                            nav.navigate(Route.Siguiente.to(nombre, fono)) {
                                popUpTo(Route.Login.path) { inclusive = true }
                            }
                        },
                        onBack = { nav.popBackStack() }
                    )
                }

                // Pantalla principal: Siguiente(nombre, fono)
                composable(
                    route = Route.Siguiente.path,
                    arguments = listOf(
                        navArgument("nombre") { type = NavType.StringType },
                        navArgument("fono") { type = NavType.StringType }
                    )
                ) { backStack ->
                    val nombre = backStack.arguments?.getString("nombre").orEmpty()
                    val fono = backStack.arguments?.getString("fono").orEmpty()

                    // Demo de ofertas. En producción: venir desde VM/Repo.
                    val demo = listOf(
                        Oferta("1","Combo 1","Aprovecha ya !","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo1.jpg"),
                        Oferta("2","Combo 2","Delicioso","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo2.jpg"),
                        Oferta("3","Combo 3","Con credencial","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo3.jpg")
                    )

                    SiguienteScreen(
                        nombre = nombre,
                        fono = fono,
                        onBack = { nav.popBackStack() },
                        ofertas = demo,
                        onClickOferta = { /* navegar a detalle si procede */ }
                    )
                }

                // Pantalla: Perfil
                composable(Route.Perfil.path) {
                    com.example.barlacteo_manuel_caceres.ui.profile.ProfileScreen(
                        onBack = { nav.popBackStack() }
                    )
                }

                // Pantalla: Catálogo
                composable(Route.Catalog.path) {
                    CatalogScreen(
                        csvUrl = "https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/catalogo_fronted.csv",
                        onBack = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}
