package com.example.barlacteo_manuel_caceres.ui.checkout

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.remote.NetworkModule
import kotlinx.coroutines.launch
import com.example.barlacteo_manuel_caceres.data.remote.PedidoRequest
class CheckoutViewModel : ViewModel() {
    private val api = NetworkModule.usuariosApi
    fun pagar(context: Context, fono: String, total: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = PedidoRequest(
                    fonoUsuario = fono,
                    total = total,
                    items = emptyList()
                )
                val response = api.iniciarPedido(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    val urlWebpay = body?.get("url")
                    val token = body?.get("token_ws")

                    if (urlWebpay != null) {
                        println("ABRIENDO WEBPAY: $urlWebpay")
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlWebpay))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        onSuccess()
                    }
                } else {
                    println("Error en pedido: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}