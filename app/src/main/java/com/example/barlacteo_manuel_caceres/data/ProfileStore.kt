package com.example.barlacteo_manuel_caceres.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.barlacteo_manuel_caceres.model.Profile
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "profile_prefs")

object ProfileStoreKeys {
    val NOMBRE = stringPreferencesKey("nombre")
    val FONO = stringPreferencesKey("fono")
    val FOTO = stringPreferencesKey("foto_uri")
}

class ProfileRepository(private val context: Context) {
    val profileFlow = context.dataStore.data.map { p ->
        Profile(
            nombre = p[ProfileStoreKeys.NOMBRE].orEmpty(),
            fono = p[ProfileStoreKeys.FONO].orEmpty(),
            fotoUri = p[ProfileStoreKeys.FOTO].orEmpty()
        )
    }
    suspend fun save(profile: Profile) {
        context.dataStore.edit { e ->
            e[ProfileStoreKeys.NOMBRE] = profile.nombre
            e[ProfileStoreKeys.FONO] = profile.fono
            e[ProfileStoreKeys.FOTO] = profile.fotoUri
        }
    }
}
