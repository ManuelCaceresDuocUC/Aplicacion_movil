package com.example.barlacteo_manuel_caceres.data.repository

import com.example.barlacteo_manuel_caceres.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.charset.Charset

/**
 * Lee un CSV público y lo mapea a [Producto].
 * Columnas esperadas: title, description, price, image_link, category.
 */
class CatalogRepository(
    private val csvUrl: String
) {
    // Cliente reutilizable. Suma timeouts básicos para robustez.
    private val client = OkHttpClient.Builder()
        //.callTimeout(Duration.ofSeconds(15))
        //.connectTimeout(Duration.ofSeconds(10))
        //.readTimeout(Duration.ofSeconds(15))
        //.writeTimeout(Duration.ofSeconds(10))
        .build()

    /**
     * Descarga y parsea el CSV en hilo de I/O.
     * Devuelve Result<List<Producto>> para no lanzar fuera.
     */
    suspend fun fetchProductos(): Result<List<Producto>> = runCatching {
        withContext(Dispatchers.IO) {
            val req = Request.Builder().url(csvUrl).build()

            // Asegura cierre del Response para no filtrar conexiones.
            client.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) error("HTTP ${resp.code} al leer CSV")
                val bytes = resp.body?.bytes() ?: error("Respuesta vacía")

                // Intento UTF-8. Si no calza, cae a ISO-8859-1.
                val text = decodeBestEffort(bytes)

                // Divide por líneas y limpia CR. Omite vacías.
                val lines = text
                    .split('\n')
                    .map { it.trim('\r') }
                    .filter { it.isNotBlank() }

                if (lines.isEmpty()) return@use emptyList<Producto>()

                // Detecta separador simple (; o ,). No maneja comillas RFC 4180.
                val sep = if (lines.first().contains(';')) ';' else ','

                val header = lines.first().split(sep).map { it.trim() }

                fun idx(name: String) = header.indexOf(name).takeIf { it >= 0 }
                val iTitle = idx("title") ?: error("Columna 'title' no encontrada")
                val iDesc  = idx("description") ?: error("Columna 'description' no encontrada")
                val iPrice = idx("price") ?: error("Columna 'price' no encontrada")
                val iImg   = idx("image_link") ?: error("Columna 'image_link' no encontrada")
                val iCat   = idx("category") ?: error("Columna 'category' no encontrada")

                // Mapea filas. Descarta las cortas.
                lines.drop(1).mapNotNull { line ->
                    val cols = line.split(sep)
                    if (cols.size < header.size) return@mapNotNull null

                    Producto(
                        title       = cols[iTitle].trim(),
                        description = cols[iDesc].trim(),
                        price       = cols[iPrice].trim(),
                        imageUrl    = cols[iImg].trim(),
                        category    = cols[iCat].trim()
                    )
                }
            }
        }
    }.recoverCatching { e ->
        // Uniformiza el error hacia arriba sin perder el Result.
        throw IllegalStateException("No se pudo leer el catálogo: ${e.message ?: e::class.simpleName}")
    }

    /**
     * Intenta decodificar en UTF-8 con BOM si existe.
     * Si no parece UTF-8, cae a ISO-8859-1.
     */
    private fun decodeBestEffort(bytes: ByteArray): String {
        // Maneja BOM UTF-8 (EF BB BF) si viene.
        val hasBom = bytes.size >= 3 &&
                bytes[0] == 0xEF.toByte() &&
                bytes[1] == 0xBB.toByte() &&
                bytes[2] == 0xBF.toByte()

        val utf8 = try {
            val slice = if (hasBom) bytes.copyOfRange(3, bytes.size) else bytes
            String(slice, Charsets.UTF_8)
        } catch (_: Exception) { null }

        // Heurística mínima: si trae "title" parece decente.
        return if (utf8 != null && utf8.contains("title", ignoreCase = true)) utf8
        else String(bytes, Charset.forName("ISO-8859-1"))
    }
}
