package com.example.barlacteo_manuel_caceres.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository
import com.example.barlacteo_manuel_caceres.data.validation.AuthValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val nombre: String = "",
    val fono: String = "",
    val isNombreValid: Boolean = false,
    val isFonoValid: Boolean = false,
    val canSubmit: Boolean = false,
    val errorNombre: String? = null,
    val errorFono: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(private val repo: AccountRepository): ViewModel() {
    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun updateNombre(v: String) = _state.update { s ->
        val ok = AuthValidator.nombre(v)
        s.copy(
            nombre = v,
            isNombreValid = ok,
            errorNombre = if (ok) null else "Mínimo 3 caracteres",
            canSubmit = ok && s.isFonoValid,
            error = null
        )
    }

    fun updateFono(v: String) = _state.update { s ->
        val ok = AuthValidator.fono(v)
        s.copy(
            fono = v,
            isFonoValid = ok,
            errorFono = if (ok) null else "Formato: +569########",
            canSubmit = ok && s.isNombreValid ,
            error = null
        )
    }



    fun login(onSuccess: () -> Unit) = viewModelScope.launch {
        val s = _state.value
        if (!s.canSubmit) return@launch
        _state.update { it.copy(loading = true, error = null) }
        val r = repo.login(s.nombre.trim(), s.fono.trim())
        _state.update { it.copy(loading = false, error = r.exceptionOrNull()?.message) }
        if (r.isSuccess) onSuccess()
    }

    fun register(onSuccess: () -> Unit) = viewModelScope.launch {
        val s = _state.value
        if (!s.canSubmit) return@launch
        _state.update { it.copy(loading = true, error = null) }
        val r = repo.register(s.nombre.trim(), s.fono.trim())
        _state.update { it.copy(loading = false, error = r.exceptionOrNull()?.message) }
        if (r.isSuccess) onSuccess()
    }
}

class AuthVMFactory(private val repo: AccountRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}
