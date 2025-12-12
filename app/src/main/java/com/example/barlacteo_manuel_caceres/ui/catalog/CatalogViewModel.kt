package com.example.barlacteo_manuel_caceres.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.repository.CatalogRepository
import com.example.barlacteo_manuel_caceres.domain.model.Producto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CatalogUiState(
    val loading: Boolean = false,
    val items: List<Producto> = emptyList(),
    val error: String? = null,
    val categoryFilter: String? = null,
    val query: String = ""
)

class CatalogViewModel(private val repo: CatalogRepository) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState(loading = true))
    val state: StateFlow<CatalogUiState> = _state

    private var searchJob: Job? = null

    init {
        cargarDatos()
    }

    private fun cargarDatos(categoria: String? = null, busqueda: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)

            val resultado = repo.fetchProductos(categoria, busqueda)

            if (resultado.isSuccess) {
                _state.value = _state.value.copy(
                    loading = false,
                    items = resultado.getOrDefault(emptyList())
                )
            } else {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "Error: ${resultado.exceptionOrNull()?.message}"
                )
            }
        }
    }

    fun setCategory(cat: String?) {
        _state.value = _state.value.copy(categoryFilter = cat)
        cargarDatos(categoria = cat, busqueda = _state.value.query)
    }

    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            cargarDatos(categoria = _state.value.categoryFilter, busqueda = q)
        }
    }

    fun refresh() {
        setCategory(null)
        setQuery("")
        cargarDatos()
    }
}

class CatalogVMFactory(private val repo: CatalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CatalogViewModel(repo) as T
    }
}