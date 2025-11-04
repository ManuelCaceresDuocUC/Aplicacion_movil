package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository
import com.example.barlacteo_manuel_caceres.domain.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel de Perfil.
 *
 * Responsabilidades:
 * - Exponer el estado de la pantalla como un StateFlow<Profile>.
 * - Escuchar cambios del repositorio y reflejarlos en la UI.
 * - Orquestar actualizaciones de nombre, fono y foto.
 * - Persistir el perfil al repositorio.
 *
 * Invariante buscada:
 * - El StateFlow siempre emite un objeto Profile válido.
 */
class ProfileViewModel(
    private val repo: ProfileRepository,
    private val accountRepo: AccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(Profile())
    val state: StateFlow<Profile> = _state

    init {
        viewModelScope.launch {
            repo.profileFlow.collect { repoProfile ->
                _state.value = repoProfile
            }
        }
    }

    fun updateNombre(v: String) = _state.update { it.copy(nombre = v) }
    fun updateFono(v: String)    = _state.update { it.copy(fono = v) }
    fun updateFoto(uri: String)  = _state.update { it.copy(fotoUri = uri) }
    fun prefill(nombre: String, fono: String) {
        _state.update { it.copy(nombre = nombre, fono = fono) }
    }

    fun save() = viewModelScope.launch {
        val profile = _state.value
        // 1) guarda perfil en DataStore de perfil
        repo.save(profile)
        // 2) sincroniza/actualiza la cuenta actual
        accountRepo.upsertAndSetCurrent(profile.nombre, profile.fono)
    }
}