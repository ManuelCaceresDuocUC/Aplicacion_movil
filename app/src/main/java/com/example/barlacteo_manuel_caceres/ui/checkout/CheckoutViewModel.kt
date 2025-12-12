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

                // AHORA (Solución rápida con Mapa):
                val request = mapOf(
                    "fonoUsuario" to fono,
                    "total" to total,
                    "items" to emptyList<Any>()
                )

                // Llamamos a la API enviando el mapa
                val response = api.iniciarPedido(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    val urlWebpay = body?.get("url")
                    // ... (resto del código igual)
                    if (urlWebpay != null) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlWebpay))
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}