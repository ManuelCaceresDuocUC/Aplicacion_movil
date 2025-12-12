package com.example.barlacteo_manuel_caceres.ui.nav

import android.net.Uri

/**
 * Mapa tipado de rutas de la app.
 *
 * Convenciones:
 * - `path` es el patrón de navegación para Compose Navigation.
 * - Si la ruta recibe datos de usuario, usa helpers `to(...)` con `Uri.encode`.
 * - Prefiere query params cuando el payload puede crecer mucho.
 */
sealed class Route(val path: String) {

    data object Login : Route("login")
    data object Register : Route("register")
    data object Catalog : Route("catalog")
    data object Siguiente : Route("siguiente/{nombre}/{fono}") {

        fun to(nombre: String, fono: String): String =
            "siguiente/${Uri.encode(nombre)}/${Uri.encode(fono)}"

        fun fromArgs(get: (String) -> String?): Pair<String, String> {
            val nombre = get("nombre").orEmpty()
            val fono = get("fono").orEmpty()
            return nombre to fono
        }
    }

    data object Perfil : Route("perfil")
}
