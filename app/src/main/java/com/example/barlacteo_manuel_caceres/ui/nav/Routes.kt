package com.example.barlacteo_manuel_caceres.ui.nav

import android.net.Uri

sealed class Route(val path: String) {
    data object Home : Route("home")

    data object Siguiente : Route("siguiente/{nombre}/{fono}") {
        fun to(nombre: String, fono: String): String =
            "siguiente/${Uri.encode(nombre)}/${Uri.encode(fono)}"
    }
}
