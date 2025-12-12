package com.example.barlacteo_manuel_caceres.data.repository

import com.example.barlacteo_manuel_caceres.data.remote.NetworkModule
import com.example.barlacteo_manuel_caceres.domain.model.Profile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.example.barlacteo_manuel_caceres.domain.model.PedidoUsuario
class ProfileRepository {

    private val api = NetworkModule.usuariosApi

    suspend fun actualizarPerfil(idUsuario: Long, nombre: String, fono: String): Result<Unit> {
        return try {
            val datos = mapOf(
                "nombre" to nombre,
                "fono" to fono
            )

            val response = api.actualizarPerfil(idUsuario, datos)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al guardar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun subirFoto(id: Long, imagen: MultipartBody.Part): Result<String> {
        return try {
            val response = api.subirFoto(id, imagen)

            if (response.isSuccessful) {
                val urlNueva = response.body()?.get("url") ?: ""
                Result.success(urlNueva)
            } else {
                Result.failure(Exception("Error subida"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun obtenerHistorial(fono: String): Result<List<PedidoUsuario>> {
        return try {
            val response = api.misPedidos(fono)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error al cargar pedidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}