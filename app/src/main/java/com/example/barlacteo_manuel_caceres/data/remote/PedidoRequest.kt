package com.example.barlacteo_manuel_caceres.data.remote

data class PedidoRequest(
    val fonoUsuario: String,
    val total: Int,
    val items: List<String> = emptyList()
)