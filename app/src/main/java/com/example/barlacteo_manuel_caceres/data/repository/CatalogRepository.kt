package com.example.barlacteo_manuel_caceres.data.repository

import com.example.barlacteo_manuel_caceres.data.remote.NetworkModule
import com.example.barlacteo_manuel_caceres.domain.model.Producto

class CatalogRepository {

    private val api = NetworkModule.catalogoApi

    suspend fun fetchProductos(categoria: String? = null, query: String? = null): Result<List<Producto>> {
        return try {
            val response = api.obtenerProductos(categoria, query)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}