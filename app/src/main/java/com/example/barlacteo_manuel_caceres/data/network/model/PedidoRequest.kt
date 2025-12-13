package com.example.barlacteo_manuel_caceres.data.network.model

import com.google.gson.annotations.SerializedName

data class PedidoRequest(
    @SerializedName("fono") val fono: String,
    @SerializedName("total") val total: Int,
    @SerializedName("productos") val productos: List<ProductoPedidoDto>
)

data class ProductoPedidoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cantidad") val cantidad: Int
)