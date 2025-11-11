package com.example.barlacteo_manuel_caceres.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.barlacteo_manuel_caceres.ui.catalog.CatalogScreen
import com.example.barlacteo_manuel_caceres.domain.model.Oferta
import com.example.barlacteo_manuel_caceres.ui.auth.LoginScreen
import com.example.barlacteo_manuel_caceres.ui.auth.RegisterScreen
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository
import com.example.barlacteo_manuel_caceres.ui.principal.SiguienteScreen

// carrito
import com.example.barlacteo_manuel_caceres.data.local.CartStore
import com.example.barlacteo_manuel_caceres.data.repository.CartRepository
import com.example.barlacteo_manuel_caceres.ui.cart.CartSidePanel
import com.example.barlacteo_manuel_caceres.ui.cart.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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

    // userId para el carrito: usa fono si lo tienes; si no, “guest”
    val userId = (currentAccount?.fono ?: "guest")

    // VM del carrito: vive aquí para que el FAB aparezca en todas las pantallas post-login
    val cartVm = remember(userId) {
        val repo = CartRepository(CartStore(ctx), CoroutineScope(SupervisorJob() + Dispatchers.IO))
        CartViewModel(repo, userId)
    }
    val cart by cartVm.state.collectAsState()

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
                            accountRepo.logout()
                            drawerState.close()
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
            },
            floatingActionButton = {
                if (!isLoginOrRegister) {
                    FloatingActionButton(onClick = { cartVm.togglePanel() }) {
                        BadgedBox(badge = { if (cart.count > 0) Badge { Text(cart.count.toString()) } }) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                }
            }
        ) { inner ->
            // Capa para poder superponer el sidebar del carrito sobre el contenido
            Box(Modifier.padding(inner)) {
                NavHost(
                    navController = nav,
                    startDestination = Route.Login.path,
                    modifier = modifier
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
                    composable(
                        route = Route.Siguiente.path,
                        arguments = listOf(
                            navArgument("nombre") { type = NavType.StringType },
                            navArgument("fono") { type = NavType.StringType }
                        )
                    ) { backStack ->
                        val nombre = backStack.arguments?.getString("nombre").orEmpty()
                        val fono = backStack.arguments?.getString("fono").orEmpty()
                        val demo = listOf(
                            Oferta("1","Combo 1","Aprovecha ya !","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo1.jpg"),
                            Oferta("2","Combo 2","Delicioso","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo2.jpg"),
                            Oferta("3","Combo 3","Con credencial","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo3.jpg")
                        )
                        com.example.barlacteo_manuel_caceres.ui.principal.SiguienteScreen(
                            nombre = nombre,
                            fono = fono,
                            onBack = { nav.popBackStack() },
                            ofertas = demo,
                            onClickOferta = { /* opcional */ }
                        )
                    }
                    composable(Route.Perfil.path) {
                        com.example.barlacteo_manuel_caceres.ui.profile.ProfileScreen(
                            onBack = { nav.popBackStack() }
                        )
                    }
                    composable(Route.Catalog.path) {
                        // Pasa el VM para que las tarjetas puedan agregar al carrito
                        CatalogScreen(
                            csvUrl = "https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/catalogo_fronted.csv",
                            onBack = { nav.popBackStack() },
                            cartVm = cartVm
                        )
                    }
                }

                // Sidebar del carrito
                CartSidePanel(
                    open = cart.isPanelOpen,
                    items = cart.items,
                    totalCents = cart.totalCents,
                    onClose = cartVm::closePanel,
                    onInc = cartVm::inc,
                    onDec = cartVm::dec,
                    onRemove = cartVm::remove,
                    onClear = cartVm::clear,
                    onCheckout = { /* TODO checkout */ }
                )
            }
        }
    }
}
