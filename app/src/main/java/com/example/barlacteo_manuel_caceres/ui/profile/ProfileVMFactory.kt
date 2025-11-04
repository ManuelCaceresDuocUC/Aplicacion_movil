package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository

/**
 * Factory para crear ProfileViewModel con su dependencia ProfileRepository.
 *
 * Útil cuando no usas Hilt. Permite inyectar el repo al VM en Compose:
 *   val vm: ProfileViewModel = viewModel(factory = ProfileVMFactory(repo))
 */
class ProfileVMFactory(
    private val profileRepo: ProfileRepository,
    private val accountRepo: AccountRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(profileRepo, accountRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}