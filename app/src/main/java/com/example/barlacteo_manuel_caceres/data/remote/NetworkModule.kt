package com.example.barlacteo_manuel_caceres.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private const val BASE_URL_USUARIOS = "https://microservicio-usuarios-production-4fdd.up.railway.app"
    private const val BASE_URL_CATALOGO = "https://microservicio-catalogo-production.up.railway.app"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofitUsuarios = Retrofit.Builder()
        .baseUrl(BASE_URL_USUARIOS)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuariosApi: UsuariosApiService = retrofitUsuarios.create(UsuariosApiService::class.java)

    private val retrofitCatalogo = Retrofit.Builder()
        .baseUrl(BASE_URL_CATALOGO)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val catalogoApi: CatalogoApiService = retrofitCatalogo.create(CatalogoApiService::class.java)
}