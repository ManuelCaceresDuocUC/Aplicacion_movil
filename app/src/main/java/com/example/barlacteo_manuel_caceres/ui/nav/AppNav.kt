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
import com.example.barlacteo_manuel_caceres.ui.screens.registro.HomeScreen
import com.example.barlacteo_manuel_caceres.ui.screens.principal.SiguienteScreen
import kotlinx.coroutines.launch
import com.example.barlacteo_manuel_caceres.model.Oferta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val backEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route.orEmpty()
    val isHome = currentRoute == Route.Home.path

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isHome,                 // sin gesto en Home
        drawerContent = {
            ModalDrawerSheet {
                Text("BarLácteo", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Registro") },
                    selected = isHome,
                    onClick = {
                        nav.navigate(Route.Home.path) {
                            popUpTo(nav.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    selected = currentRoute.startsWith("siguiente"),
                    onClick = {
                        nav.navigate(Route.Siguiente.to("Invitado", "+56912345678")) { launchSingleTop = true }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isHome) "Registro" else "BarLácteo") },
                    navigationIcon = {
                        if (!isHome) { // no mostrar menú en Home
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
                startDestination = Route.Home.path,
                modifier = modifier.padding(inner)
            ) {
                composable(Route.Home.path) {
                    HomeScreen(
                        onContinue = { nombre, fono ->
                            nav.navigate(Route.Siguiente.to(nombre, fono))
                        }
                    )
                }
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
                        Oferta("1","2x1 Sándwich","Solo hoy","https://picsum.photos/seed/ba1/900/500"),
                        Oferta("2","Combo Café + Brownie","12:00-14:00","https://picsum.photos/seed/ba2/900/500"),
                        Oferta("3","Descuento estudiantes","Con credencial","https://picsum.photos/seed/ba3/900/500"),
                    )

                    SiguienteScreen(
                        nombre = nombre,
                        fono = fono,
                        onBack = { nav.popBackStack() },
                        ofertas = demo,
                        onClickOferta = { /* navegar a detalle */ }
                    )
                }

            }
        }
    }
}

