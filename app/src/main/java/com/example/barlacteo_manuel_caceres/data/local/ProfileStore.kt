package com.example.barlacteo_manuel_caceres.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.barlacteo_manuel_caceres.domain.model.Profile
import kotlinx.coroutines.flow.map

// DataStore (scoped a Context) para guardar pares clave/valor del perfil.
private val Context.dataStore by preferencesDataStore(name = "profile_prefs")

/**
 * Llaves tipadas para evitar strings mágicos.
 */
object ProfileStoreKeys {
    val NOMBRE = stringPreferencesKey("nombre")
    val FONO   = stringPreferencesKey("fono")
    val FOTO   = stringPreferencesKey("foto_uri")
}

/**
 * Repositorio de perfil sobre DataStore Preferences.
 * Lee y guarda nombre, fono y fotoUri.
 */
class ProfileRepository(private val context: Context) {

    /**
     * Flujo continuo del perfil. Emite cada vez que cambian las prefs.
     * Si no existe una clave, usa "" para no romper la UI.
     */
    val profileFlow = context
        .dataStore
        .data
        .map { p ->
            Profile(
                nombre  = p[ProfileStoreKeys.NOMBRE].orEmpty(),
                fono    = p[ProfileStoreKeys.FONO].orEmpty(),
                fotoUri = p[ProfileStoreKeys.FOTO].orEmpty()
            )
        }

    /**
     * Persiste el perfil completo en una transacción edit().
     * Operación suspend por I/O.
     */
    suspend fun save(profile: Profile) {
        context.dataStore.edit { e ->
            e[ProfileStoreKeys.NOMBRE] = profile.nombre
            e[ProfileStoreKeys.FONO]   = profile.fono
            e[ProfileStoreKeys.FOTO]   = profile.fotoUri
        }
    }
}
