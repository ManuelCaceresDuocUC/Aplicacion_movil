// Asegúrate de que esta línea coincida con tu estructura
package com.example.barlacteo_manuel_caceres.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    fun comprimirImagen(context: Context, imageUri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Redimensionar para que el lado más largo sea máximo 1024px
        val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        val maxWidth = 1024
        val newWidth: Int
        val newHeight: Int

        if (originalBitmap.width > originalBitmap.height) {
            newWidth = maxWidth
            newHeight = (maxWidth / ratio).toInt()
        } else {
            newHeight = maxWidth
            newWidth = (maxWidth * ratio).toInt()
        }

        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        // Crear archivo en caché
        val file = File(context.cacheDir, "foto_perfil_compressed.jpg")
        val outStream = FileOutputStream(file)

        // Comprimir a JPG al 75% de calidad
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outStream)

        outStream.flush()
        outStream.close()

        return file
    }
}