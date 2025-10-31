package com.example.barlacteo_manuel_caceres.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val nombre: String = "",
    val fono: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val repo: AccountRepository): ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun updateNombre(v: String) { _state.value = _state.value.copy(nombre = v, error = null) }
    fun updateFono(v: String) { _state.value = _state.value.copy(fono = v, error = null) }

    fun login(onSuccess: () -> Unit) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        try {
            val s = _state.value
            val r = repo.login(s.nombre.trim(), s.fono.trim())
            _state.value = _state.value.copy(
                loading = false,
                error = r.exceptionOrNull()?.message
            )
            if (r.isSuccess) onSuccess()
        } catch (e: Exception) {
            _state.value = _state.value.copy(loading = false, error = e.message ?: "Error inesperado")
        }
    }

    fun register(onSuccess: () -> Unit) = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        val s = _state.value
        val r = repo.register(s.nombre.trim(), s.fono.trim())
        _state.value = _state.value.copy(loading = false, error = r.exceptionOrNull()?.message)
        if (r.isSuccess) onSuccess()
    }
}

class AuthVMFactory(private val repo: AccountRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}
