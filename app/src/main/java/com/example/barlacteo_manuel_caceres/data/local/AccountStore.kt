package com.example.barlacteo_manuel_caceres.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.barlacteo_manuel_caceres.data.remote.NetworkModule
import com.example.barlacteo_manuel_caceres.domain.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore solo para guardar la sesión activa
private val Context.dataStore by preferencesDataStore(name = "session_prefs")
private val KEY_USER_NAME = stringPreferencesKey("user_name")
private val KEY_USER_FONO = stringPreferencesKey("user_fono")

class AccountRepository(private val context: Context) {

    private val api = NetworkModule.usuariosApi

    val currentAccountFlow: Flow<Account?> = context.dataStore.data.map { prefs ->
        val nombre = prefs[KEY_USER_NAME]
        val fono = prefs[KEY_USER_FONO]
        if (nombre != null && fono != null) {
            Account(nombre, fono)
        } else {
            null
        }
    }

    suspend fun register(nombre: String, fono: String): Result<Unit> {
        return try {
            val request = mapOf("nombre" to nombre, "fono" to fono)

            val response = api.registrar(request)

            if (response.isSuccessful) {
                saveSessionLocal(nombre, fono)
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al registrar"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun login(nombre: String, fono: String): Result<Unit> {
        return try {
            val request = mapOf("nombre" to nombre, "fono" to fono)

            val response = api.login(request)

            if (response.isSuccessful) {
                saveSessionLocal(nombre, fono)
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Credenciales incorrectas"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
    private suspend fun saveSessionLocal(nombre: String, fono: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = nombre
            prefs[KEY_USER_FONO] = fono
        }
    }
}