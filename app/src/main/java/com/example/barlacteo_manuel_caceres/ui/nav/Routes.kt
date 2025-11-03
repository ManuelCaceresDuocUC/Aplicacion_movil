package com.example.barlacteo_manuel_caceres.ui.nav

import android.net.Uri

sealed class Route(val path: String) {
    data object Login : Route("login")
    data object Register : Route("register")
    data object Catalog : Route("catalog")

    data object Home : Route("home") // si aún la usas
    data object Siguiente : Route("siguiente/{nombre}/{fono}") {
        fun to(nombre: String, fono: String) =
            "siguiente/${Uri.encode(nombre)}/${Uri.encode(fono)}"
    }
    data object Perfil : Route("perfil")
}
