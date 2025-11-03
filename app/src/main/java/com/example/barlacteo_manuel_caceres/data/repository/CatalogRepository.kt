package com.example.barlacteo_manuel_caceres.data.repository

import com.example.barlacteo_manuel_caceres.domain.model.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class CatalogRepository(
    private val csvUrl: String
) {
    private val client = OkHttpClient()

    suspend fun fetchProductos(): Result<List<Producto>> = runCatching {
        withContext(Dispatchers.IO) {      // 🔹 mover todo el bloque a hilo de IO
            val req = Request.Builder().url(csvUrl).build()
            val resp = client.newCall(req).execute()
            if (!resp.isSuccessful) error("HTTP ${resp.code} al leer CSV")
            val bytes = resp.body?.bytes() ?: error("Respuesta vacía")

            val textUtf8 = try {
                String(bytes, Charsets.UTF_8)
            } catch (_: Exception) {
                null
            }
            val text = if (textUtf8 != null && textUtf8.contains("title", true)) textUtf8
            else String(bytes, Charsets.ISO_8859_1)

            val lines = text.split('\n').map { it.trim('\r') }.filter { it.isNotBlank() }
            if (lines.isEmpty()) return@withContext emptyList<Producto>()

            val sep = if (lines.first().contains(';')) ';' else ','
            val header = lines.first().split(sep).map { it.trim() }

            fun idx(name: String) = header.indexOf(name).takeIf { it >= 0 }
            val iTitle = idx("title") ?: error("Columna 'title' no encontrada")
            val iDesc = idx("description") ?: error("Columna 'description' no encontrada")
            val iPrice = idx("price") ?: error("Columna 'price' no encontrada")
            val iImg = idx("image_link") ?: error("Columna 'image_link' no encontrada")
            val iCat = idx("category") ?: error("Columna 'category' no encontrada")

            lines.drop(1).mapNotNull { line ->
                val cols = line.split(sep)
                if (cols.size < header.size) return@mapNotNull null
                Producto(
                    title = cols[iTitle].trim(),
                    description = cols[iDesc].trim(),
                    price = cols[iPrice].trim(),
                    imageUrl = cols[iImg].trim(),
                    category = cols[iCat].trim()
                )
            }
        }
    }.recoverCatching { e ->
        throw IllegalStateException("No se pudo leer el catálogo: ${e.message ?: e::class.simpleName}")
    }
}