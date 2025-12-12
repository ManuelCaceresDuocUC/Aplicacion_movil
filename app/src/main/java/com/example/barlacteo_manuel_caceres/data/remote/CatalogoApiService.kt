package com.example.barlacteo_manuel_caceres.data.remote


import com.example.barlacteo_manuel_caceres.domain.model.Producto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogoApiService {
    @GET("api/productos")
    suspend fun obtenerProductos(
        @Query("categoria") categoria: String? = null,
        @Query("busqueda") busqueda: String? = null
    ): Response<List<Producto>>
}