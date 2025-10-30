package com.example.barlacteo_manuel_caceres.ui.nav
import android.net.Uri

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Siguiente : Route("siguiente?nombre={nombre}&fono={fono}") {
        fun build(nombre: String, fono: String) =
            "siguiente?nombre=${Uri.encode(nombre)}&fono=${Uri.encode(fono)}"
    }
}
