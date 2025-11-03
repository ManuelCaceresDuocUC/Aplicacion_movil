package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository

class ProfileVMFactory(private val repo: ProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repo) as T
    }
}
