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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val backEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route.orEmpty()
    val isLoginOrRegister = currentRoute == Route.Login.path || currentRoute == Route.Register.path
    val ctx = LocalContext.current
    val accountRepo = remember { AccountRepository(ctx) }
    val currentAccount by remember { accountRepo.currentAccountFlow }.collectAsState(initial = null)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isLoginOrRegister,
        drawerContent = {
            ModalDrawerSheet {
                Text("BarLácteo", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
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
                NavigationDrawerItem(
                    label = { Text("Perfil") },
                    selected = currentRoute == Route.Perfil.path,
                    onClick = {
                        nav.navigate(Route.Perfil.path) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Catálogo") },
                    selected = currentRoute == Route.Catalog.path,
                    onClick = {
                        nav.navigate(Route.Catalog.path) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            // 1) limpiar sesión
                            accountRepo.logout()
                            // 2) cerrar el drawer antes de navegar
                            drawerState.close()
                            // 3) navegar a Login limpiando el back stack
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
                    title = { Text( when {
                        currentRoute == Route.Login.path -> "Inicio de sesión"
                        currentRoute == Route.Register.path -> "Crear cuenta"
                        else -> "BarLácteo"
                    }) },
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
            NavHost(
                navController = nav,
                startDestination = Route.Login.path,
                modifier = modifier.padding(inner)
            ) {
                composable(Route.Login.path) {
                    LoginScreen(
                        onLoginOk = { nombre, fono ->
                            nav.navigate(Route.Siguiente.to(nombre, fono)) {
                                popUpTo(Route.Login.path) { inclusive = true }
                            }
                        },
                        onGoRegister = { nav.navigate(Route.Register.path) }
                    )
                }
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
                // tu pantalla principal
                composable(
                    route = Route.Siguiente.path,
                    arguments = listOf(
                        navArgument("nombre"){ type = NavType.StringType },
                        navArgument("fono"){ type = NavType.StringType }
                    )
                ) { backStack ->
                    val nombre = backStack.arguments?.getString("nombre").orEmpty()
                    val fono = backStack.arguments?.getString("fono").orEmpty()
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
                        onClickOferta = { }
                    )
                }
                composable(Route.Perfil.path) {
                    com.example.barlacteo_manuel_caceres.ui.profile.ProfileScreen(
                        onBack = { nav.popBackStack() }
                    )
                }
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
