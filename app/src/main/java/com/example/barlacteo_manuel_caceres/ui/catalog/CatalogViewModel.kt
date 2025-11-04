package com.example.barlacteo_manuel_caceres.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.repository.CatalogRepository
import com.example.barlacteo_manuel_caceres.domain.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ===== UI State =====

/**
 * Estado de la pantalla de catálogo.
 * @param loading indica carga en progreso.
 * @param items productos crudos obtenidos del repo.
 * @param error mensaje de error si algo falla.
 * @param categoryFilter filtro exacto por categoría, null = todas.
 * @param query filtro de búsqueda por texto libre.
 */
data class CatalogUiState(
    val loading: Boolean = false,
    val items: List<Producto> = emptyList(),
    val error: String? = null,
    val categoryFilter: String? = null,
    val query: String = ""
)

// ===== ViewModel =====

/**
 * Orquesta la carga de productos y filtros de búsqueda/categoría.
 * Fuente de verdad: repo.fetchProductos().
 */
class CatalogViewModel(private val repo: CatalogRepository) : ViewModel() {

    // Estado interno mutable. Solo el VM lo toca.
    private val _state = MutableStateFlow(CatalogUiState(loading = true))

    // Exposición inmutable para la UI.
    val state: StateFlow<CatalogUiState> = _state

    init {
        // Carga inicial.
        refresh()
    }

    /**
     * Refresca la lista de productos desde el repositorio.
     * Maneja flags de loading y error.
     */
    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        val r = repo.fetchProductos()
        _state.value =
            if (r.isSuccess) {
                _state.value.copy(
                    loading = false,
                    items = r.getOrNull().orEmpty()
                )
            } else {
                _state.value.copy(
                    loading = false,
                    error = r.exceptionOrNull()?.message ?: "Error"
                )
            }
    }

    /** Ajusta el filtro de categoría. Null = sin filtro. */
    fun setCategory(cat: String?) {
        _state.value = _state.value.copy(categoryFilter = cat)
    }

    /** Ajusta la consulta de búsqueda por texto. */
    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q)
    }

    /**
     * Devuelve la lista filtrada en memoria según estado actual.
     * Regla: categoría exacta (case-insensitive) y texto en título o descripción.
     */
    fun filtered(): List<Producto> {
        val st = _state.value
        return st.items.filter {
            (st.categoryFilter == null ||
                    it.category.equals(st.categoryFilter, ignoreCase = true)) &&
                    (st.query.isBlank() ||
                            it.title.contains(st.query, ignoreCase = true) ||
                            it.description.contains(st.query, ignoreCase = true))
        }
    }
}

// ===== Factory =====

/**
 * Factory para instanciar CatalogViewModel con su repositorio.
 * Útil cuando no usas Hilt.
 */
class CatalogVMFactory(private val repo: CatalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return CatalogViewModel(repo) as T
    }
}
