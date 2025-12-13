package com.example.barlacteo_manuel_caceres.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import com.example.barlacteo_manuel_caceres.domain.model.PedidoUsuario
import com.example.barlacteo_manuel_caceres.domain.model.Account
interface UsuariosApiService {

    @POST("api/usuarios/registro")
    suspend fun registrar(@Body request: Map<String, String>): Response<Any>

    @POST("api/usuarios/login")
    suspend fun login(@Body request: Map<String, String>): Response<Account>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarPerfil(
        @Path("id") id: Long,
        @Body data: Map<String, String>
    ): Response<Any>

    @Multipart
    @POST("api/usuarios/{id}/foto")
    suspend fun subirFoto(
        @Path("id") id: Long,
        @Part imagen: MultipartBody.Part
    ): Response<Map<String, String>>

    @POST("api/pedidos/iniciar")
    suspend fun iniciarPedido(@Body request: Map<String, Any>): Response<Map<String, String>>
    @POST("api/pedidos/iniciar")
    suspend fun iniciarPedido(@Body request: PedidoRequest): Response<Map<String, String>>

    @GET("api/pedidos/usuario/{fono}")
    suspend fun misPedidos(@Path("fono") fono: String): Response<List<PedidoUsuario>>
}