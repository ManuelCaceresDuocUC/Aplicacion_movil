package com.example.barlacteo_manuel_caceres

import com.example.barlacteo_manuel_caceres.ui.catalog.CatalogViewModel
import com.example.barlacteo_manuel_caceres.MainDispatcherRule
import com.example.barlacteo_manuel_caceres.data.repository.CatalogRepository
import com.example.barlacteo_manuel_caceres.domain.model.Producto
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherRule::class) // 1. Aplicamos la regla de Corrutinas
class CatalogViewModelTest {

    // 2. MockK: Creamos el repositorio falso
    private val repo: CatalogRepository = mockk()

    // Datos de prueba (Dummy data) para usar en los tests
    private val productoQueso = Producto( title = "Queso Gouda", description = "Rico queso", category = "Lacteos", price = "10.0", imageUrl = "")
    private val productoLeche = Producto( title = "Leche Entera", description = "Leche fresca", category = "Bebidas", price = "5.0", imageUrl = "")
    private val listaPrueba = listOf(productoQueso, productoLeche)

    @Test
    fun `al iniciar, si el repo responde ok, carga los productos y quita loading`() = runTest {
        // GIVEN: El repo devolverá una lista exitosa
        coEvery { repo.fetchProductos() } returns Result.success(listaPrueba)

        // WHEN: Inicializamos el ViewModel (esto dispara el init -> refresh())
        val viewModel = CatalogViewModel(repo)

        // THEN: Verificamos el estado con Kotest
        val estado = viewModel.state.value

        estado.loading shouldBe false
        estado.error shouldBe null
        estado.items shouldBe listaPrueba
    }

    @Test
    fun `al iniciar, si el repo falla, muestra mensaje de error`() = runTest {
        // GIVEN: El repo devolverá un error
        coEvery { repo.fetchProductos() } returns Result.failure(Exception("Error de red"))

        // WHEN
        val viewModel = CatalogViewModel(repo)

        // THEN
        val estado = viewModel.state.value

        estado.loading shouldBe false
        estado.items shouldBe emptyList()
        estado.error shouldBe "Error de red"
    }

    @Test
    fun `filtered() debe filtrar correctamente por categoria`() = runTest {
        // GIVEN: Cargamos datos iniciales
        coEvery { repo.fetchProductos() } returns Result.success(listaPrueba)
        val viewModel = CatalogViewModel(repo)

        // WHEN: Filtramos por categoría "Lacteos"
        viewModel.setCategory("Lacteos")
        val resultado = viewModel.filtered()

        // THEN: Solo debe quedar el Queso
        resultado shouldContain productoQueso
        resultado shouldNotContain productoLeche
    }

    @Test
    fun `filtered() debe filtrar por texto (busqueda) ignorando mayusculas`() = runTest {
        // GIVEN
        coEvery { repo.fetchProductos() } returns Result.success(listaPrueba)
        val viewModel = CatalogViewModel(repo)

        // WHEN: Buscamos "leche" (en minúscula, aunque el título sea Leche)
        viewModel.setQuery("leche")
        val resultado = viewModel.filtered()

        // THEN
        resultado shouldContain productoLeche
        resultado shouldNotContain productoQueso
    }
}