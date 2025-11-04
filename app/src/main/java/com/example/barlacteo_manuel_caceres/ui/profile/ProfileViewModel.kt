package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository
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
class ProfileViewModel(private val repo: ProfileRepository) : ViewModel() {

    // Estado interno mutable. Solo este VM puede actualizarlo.
    private val _state = MutableStateFlow(Profile())

    // Exposición inmutable para la UI. La pantalla observa este flow.
    val state: StateFlow<Profile> = _state

    init {
        // Suscribe el VM al flujo del repositorio.
        // Cada emisión reemplaza el estado actual.
        viewModelScope.launch {
            repo.profileFlow.collect { repoProfile ->
                _state.value = repoProfile
            }
        }
    }

    // ---------- Actualizadores de campos ----------
    // Usamos copy(...) para mantener inmutabilidad de Profile.

    /** Actualiza el nombre en memoria (UI). */
    fun updateNombre(v: String) = _state.update { it.copy(nombre = v) }

    /** Actualiza el teléfono en memoria (UI). */
    fun updateFono(v: String) = _state.update { it.copy(fono = v) }

    /** Actualiza la URI de la foto en memoria (UI). */
    fun updateFoto(uri: String) = _state.update { it.copy(fotoUri = uri) }

    /**
     * Precarga nombre y fono. Útil al traer datos de otra fuente
     * como AccountRepository antes de guardar definitivo.
     */
    fun prefill(nombre: String, fono: String) {
        _state.update { it.copy(nombre = nombre, fono = fono) }
    }

    /**
     * Persiste el estado actual en el repositorio.
     * Ejecuta en corutina del VM.
     */
    fun save() = viewModelScope.launch {
        repo.save(_state.value)
    }
}
