package com.example.barlacteo_manuel_caceres.data.validation

// data/validation/AuthValidator.kt
object AuthValidator {
    fun nombre(v: String) = v.trim().length >= 3
    fun fono(v: String) = v.matches(Regex("^\\+569\\d{8}$"))
}
