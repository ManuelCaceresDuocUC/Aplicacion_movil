package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository
import com.example.barlacteo_manuel_caceres.domain.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: ProfileRepository): ViewModel() {
    private val _state = MutableStateFlow(Profile())
    val state: StateFlow<Profile> = _state

    init {
        viewModelScope.launch {
            repo.profileFlow.collect { _state.value = it }
        }
    }

    fun updateNombre(v: String) = _state.update { it.copy(nombre = v) }
    fun updateFono(v: String) = _state.update { it.copy(fono = v) }
    fun updateFoto(uri: String) = _state.update { it.copy(fotoUri = uri) }
    fun prefill(nombre: String, fono: String) {
        _state.update { it.copy(nombre = nombre, fono = fono) }
    }
    fun save() = viewModelScope.launch { repo.save(_state.value) }
}
