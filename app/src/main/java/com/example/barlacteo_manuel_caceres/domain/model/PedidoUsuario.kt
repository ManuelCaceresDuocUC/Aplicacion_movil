package com.example.barlacteo_manuel_caceres.domain.model

data class PedidoUsuario(
    val id: Long,
    val total: Int,
    val estado: String, // "PENDIENTE" o "PAGADO"
    val fecha: String? = null
)