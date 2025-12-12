package com.example.barlacteo_manuel_caceres.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.repository.ProfileRepository
import com.example.barlacteo_manuel_caceres.domain.model.PedidoUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// Estado de la UI
data class ProfileUiState(
    val nombre: String = "",
    val fono: String = "",
    val fotoUri: String = ""
)

class ProfileViewModel(
    private val repo: ProfileRepository,
    private val context: Context
) : ViewModel() {

    // Estado del Formulario
    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state

    // Estado del Historial
    private val _historial = MutableStateFlow<List<PedidoUsuario>>(emptyList())
    val historial: StateFlow<List<PedidoUsuario>> = _historial

    // ID fijo para efectos del examen (Idealmente vendría de DataStore/Session)
    private val ID_USUARIO_ACTUAL = 1L

    // --- FUNCIONES DE CAMPO ---
    fun updateNombre(v: String) = _state.update { it.copy(nombre = v) }
    fun updateFono(v: String) = _state.update { it.copy(fono = v) }

    fun prefill(nombre: String, fono: String) {
        _state.update { it.copy(nombre = nombre, fono = fono) }
    }

    // --- CARGAR HISTORIAL ---
    fun cargarHistorial(fono: String) = viewModelScope.launch {
        if (fono.isNotEmpty()) {
            val resultado = repo.obtenerHistorial(fono)
            if (resultado.isSuccess) {
                _historial.value = resultado.getOrDefault(emptyList())
            }
        }
    }

    // --- GUARDAR PERFIL ---
    fun save() = viewModelScope.launch {
        val actual = _state.value
        repo.actualizarPerfil(ID_USUARIO_ACTUAL, actual.nombre, actual.fono)
    }

    // --- SUBIR FOTO OPTIMIZADA ---
    fun subirFoto(archivo: File) = viewModelScope.launch {

        // 1. Actualizamos la vista previa inmediatamente
        _state.update { it.copy(fotoUri = android.net.Uri.fromFile(archivo).toString()) }

        try {
            // 2. Preparamos el Multipart para Retrofit
            // Le decimos que es una imagen JPEG
            val requestFile = archivo.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("imagen", archivo.name, requestFile)

            // 3. Enviamos al servidor
            val resultado = repo.subirFoto(ID_USUARIO_ACTUAL, body)

            if (resultado.isSuccess) {
                println("✅ Foto subida exitosamente: ${archivo.length() / 1024} KB")
            } else {
                println("❌ Error al subir foto")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}