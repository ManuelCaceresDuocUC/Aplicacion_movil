package com.example.barlacteo_manuel_caceres

import android.content.Context
import com.example.barlacteo_manuel_caceres.data.repository.ProfileRepository
import com.example.barlacteo_manuel_caceres.ui.profile.ProfileViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    // 1. Preparamos el ambiente para probar Corrutinas (ViewModel usa viewModelScope)
    private val testDispatcher = StandardTestDispatcher()

    // 2. Mockeamos (simulamos) las dependencias que no queremos probar realmente
    private val repoMock = mockk<ProfileRepository>(relaxed = true)
    private val contextMock = mockk<Context>(relaxed = true)

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Inicializamos el ViewModel con los Mocks
        viewModel = ProfileViewModel(repoMock, contextMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateNombre actualiza el estado correctamente`() {
        // GIVEN (Dado): Un nombre nuevo
        val nuevoNombre = "Manuel Test"

        // WHEN (Cuando): Llamamos a la función del ViewModel
        viewModel.updateNombre(nuevoNombre)

        // THEN (Entonces): El estado debe reflejar el cambio
        Assert.assertEquals("Manuel Test", viewModel.state.value.nombre)
    }

    @Test
    fun `prefill carga los datos iniciales correctamente`() {
        // GIVEN
        val nombre = "Usuario Base"
        val fono = "+56912345678"
        val foto = "https://foto.com/perfil.jpg"

        // WHEN
        viewModel.prefill(nombre, fono, foto)

        // THEN
        Assert.assertEquals(nombre, viewModel.state.value.nombre)
        Assert.assertEquals(fono, viewModel.state.value.fono)
        Assert.assertEquals(foto, viewModel.state.value.fotoUri)
    }
}