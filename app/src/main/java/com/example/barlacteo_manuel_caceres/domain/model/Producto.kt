package com.example.barlacteo_manuel_caceres.domain.model

data class Producto(
    val title: String,
    val description: String,
    val price: String,       // viene como "$3.500" en el CSV
    val imageUrl: String,
    val category: String
)
