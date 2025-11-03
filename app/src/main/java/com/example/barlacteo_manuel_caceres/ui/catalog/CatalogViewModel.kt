package com.example.barlacteo_manuel_caceres.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.barlacteo_manuel_caceres.data.repository.CatalogRepository
import com.example.barlacteo_manuel_caceres.domain.model.Producto
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

class CatalogViewModel(private val repo: CatalogRepository): ViewModel() {
    private val _state = MutableStateFlow(CatalogUiState(loading = true))
    val state: StateFlow<CatalogUiState> = _state

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = true, error = null)
        val r = repo.fetchProductos()
        _state.value = if (r.isSuccess) {
            _state.value.copy(loading = false, items = r.getOrNull().orEmpty())
        } else {
            _state.value.copy(loading = false, error = r.exceptionOrNull()?.message ?: "Error")
        }
    }

    fun setCategory(cat: String?) {
        _state.value = _state.value.copy(categoryFilter = cat)
    }

    fun setQuery(q: String) {
        _state.value = _state.value.copy(query = q)
    }

    fun filtered(): List<Producto> {
        val st = _state.value
        return st.items.filter {
            (st.categoryFilter == null || it.category.equals(st.categoryFilter, ignoreCase = true)) &&
                    (st.query.isBlank() || it.title.contains(st.query, true) || it.description.contains(st.query, true))
        }
    }
}

class CatalogVMFactory(private val repo: CatalogRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return CatalogViewModel(repo) as T
    }
}
