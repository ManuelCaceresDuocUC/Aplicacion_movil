package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository

/**
 * Factory para crear ProfileViewModel con su dependencia ProfileRepository.
 *
 * Útil cuando no usas Hilt. Permite inyectar el repo al VM en Compose:
 *   val vm: ProfileViewModel = viewModel(factory = ProfileVMFactory(repo))
 */
class ProfileVMFactory(
    private val repo: ProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // El cast es seguro tras verificar el tipo.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verificación robusta del tipo solicitado.
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repo) as T
        }
        // Si piden otro VM, falla explícito y claro.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
