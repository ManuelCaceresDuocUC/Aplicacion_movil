package com.example.barlacteo_manuel_caceres.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.barlacteo_manuel_caceres.domain.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
private val Context.dataStore by preferencesDataStore(name = "accounts_prefs")

private object Keys {
    val ACCOUNTS = stringSetPreferencesKey("accounts_set") // set de "fono|nombre"
    val CURRENT = stringPreferencesKey("current_fono")
}

class AccountRepository(private val context: Context) {

    val currentAccountFlow: Flow<Account?> =
        context.dataStore.data.map { p ->
            val fono = p[Keys.CURRENT] ?: return@map null
            val set = p[Keys.ACCOUNTS].orEmpty()
            val acc = set.firstOrNull { it.startsWith("$fono|") }
            acc?.split("|")?.let { Account(nombre = it.getOrNull(1) ?: "", fono = fono) }
        }

    suspend fun register(nombre: String, fono: String): Result<Unit> {
        if (!fono.matches(Regex("^\\+569\\d{8}$"))) return Result.failure(IllegalArgumentException("Fono inválido"))
        if (nombre.isBlank()) return Result.failure(IllegalArgumentException("Nombre vacío"))

        context.dataStore.edit { p ->
            val set = p[Keys.ACCOUNTS].orEmpty().toMutableSet()
            // si ya existe, falla
            if (set.any { it.startsWith("$fono|") }) {
                throw IllegalStateException("El fono ya está registrado")
            }
            set += "$fono|$nombre"
            p[Keys.ACCOUNTS] = set
            p[Keys.CURRENT] = fono
        }
        return Result.success(Unit)
    }

    suspend fun login(nombre: String, fono: String): Result<Unit> {
        return try {
            // Leer prefs sin editar (no lanzar excepciones)
            val prefs = context.dataStore.data.first()
            val set = prefs[Keys.ACCOUNTS].orEmpty()

            val ok = set.any { it == "$fono|$nombre" || it.startsWith("$fono|") }
            if (!ok) {
                Result.failure(IllegalStateException("Usuario no encontrado"))
            } else {
                // Solo si existe, guardamos el CURRENT
                context.dataStore.edit { p -> p[Keys.CURRENT] = fono }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        context.dataStore.edit { p -> p.remove(Keys.CURRENT) }
    }

    suspend fun accounts(): List<Account> {
        val prefs = context.dataStore.data.map { it }.first()
        return prefs[Keys.ACCOUNTS].orEmpty().mapNotNull { s ->
            val parts = s.split("|")
            if (parts.size == 2) Account(nombre = parts[1], fono = parts[0]) else null
        }
    }
    suspend fun upsertAndSetCurrent(nombre: String, fono: String): Result<Unit> {
        // valida formato si quieres mantener la misma regla
        if (!fono.matches(Regex("^\\+569\\d{8}$"))) return Result.failure(IllegalArgumentException("Fono inválido"))
        if (nombre.isBlank()) return Result.failure(IllegalArgumentException("Nombre vacío"))

        return try {
            context.dataStore.edit { p ->
                val set = p[Keys.ACCOUNTS].orEmpty().toMutableSet()
                val prev = p[Keys.CURRENT]

                // si cambió el fono respecto al CURRENT anterior, borra la entrada vieja
                if (prev != null && prev != fono) {
                    set.removeIf { it.startsWith("$prev|") }
                }

                // evita duplicados por el nuevo fono
                set.removeIf { it.startsWith("$fono|") }
                set += "$fono|$nombre"

                p[Keys.ACCOUNTS] = set
                p[Keys.CURRENT] = fono
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
