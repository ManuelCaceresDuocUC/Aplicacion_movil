package com.example.barlacteo_manuel_caceres.data.repository

import com.example.barlacteo_manuel_caceres.domain.model.Producto
import java.security.MessageDigest
import java.util.Locale

// "$3.500" | "3,500" | "3500" → 350000 (centavos)
fun String.toClpCents(): Long =
    trim().replace("$","").replace(".","").replace(",","").replace("\\s+".toRegex(),"")
        .toLongOrNull()?.times(100) ?: 0L

private fun String.sha1Hex(): String =
    MessageDigest.getInstance("SHA-1").digest(toByteArray())
        .joinToString("") { "%02x".format(it) }

// ID estable sin cambiar tu modelo
fun Producto.stableId(): String =
    "${title.lowercase(Locale.ROOT)}|${category.lowercase(Locale.ROOT)}|$price|$imageUrl"
        .sha1Hex().take(12)
