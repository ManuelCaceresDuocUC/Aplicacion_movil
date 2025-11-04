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

    // Auth
    data object Login : Route("login")
    data object Register : Route("register")

    // Catálogo
    data object Catalog : Route("catalog")



    // Post-login con datos en la URL.
    data object Siguiente : Route("siguiente/{nombre}/{fono}") {

        /**
         * Construye la ruta con encoding seguro.
         * Ejemplo:
         * nav.navigate(Route.Siguiente.to(nombre, fono))
         */
        fun to(nombre: String, fono: String): String =
            "siguiente/${Uri.encode(nombre)}/${Uri.encode(fono)}"

        /**
         * Extrae argumentos desde un mapa o bundle de Nav.
         * Útil para centralizar el manejo de defaults.
         */
        fun fromArgs(get: (String) -> String?): Pair<String, String> {
            val nombre = get("nombre").orEmpty()
            val fono = get("fono").orEmpty()
            return nombre to fono
        }
    }

    // Perfil
    data object Perfil : Route("perfil")
}
