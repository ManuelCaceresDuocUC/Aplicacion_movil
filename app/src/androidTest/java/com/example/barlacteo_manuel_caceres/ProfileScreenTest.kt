package com.example.barlacteo_manuel_caceres.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun perfil_muestra_datos_iniciales_y_permite_edicion() {
        // CORRECCIÓN: Usamos mutableStateOf para que la UI reaccione a los cambios
        // igual que lo haría en la app real.
        val nombreState = mutableStateOf("")
        val fonoState = mutableStateOf("")
        var guardado = false

        composeTestRule.setContent {
            ProfileContent(
                fotoUri = "",
                nombre = nombreState.value, // Leemos el estado
                fono = fonoState.value,
                onBack = {},
                // Al escribir, actualizamos el estado, lo que refresca la UI
                onNameChange = { nombreState.value = it },
                onFonoChange = { fonoState.value = it },
                onSaveClick = { guardado = true },
                onGalleryClick = {},
                onCameraClick = {}
            )
        }

        // WHEN: El usuario escribe su nombre
        composeTestRule
            .onNodeWithTag(ProfileTags.INPUT_NAME)
            .performTextInput("Manuel")

        // Y escribe su fono
        composeTestRule
            .onNodeWithTag(ProfileTags.INPUT_PHONE)
            .performTextInput("912345678")

        // THEN: Verificamos los valores finales
        // Usamos assertEquals para ver qué falló si vuelve a ocurrir
        assertEquals("Manuel", nombreState.value)
        assertEquals("912345678", fonoState.value)
    }

    @Test
    fun botones_de_camara_y_galeria_son_cliqueables() {
        var camaraClick = false
        var galeriaClick = false

        composeTestRule.setContent {
            ProfileContent(
                fotoUri = "",
                nombre = "",
                fono = "",
                onBack = {},
                onNameChange = {},
                onFonoChange = {},
                onSaveClick = {},
                onGalleryClick = { galeriaClick = true },
                onCameraClick = { camaraClick = true }
            )
        }

        // Click en Galería
        composeTestRule.onNodeWithTag(ProfileTags.BTN_GALLERY).performClick()
        assert(galeriaClick)

        // Click en Cámara
        composeTestRule.onNodeWithTag(ProfileTags.BTN_CAMERA).performClick()
        assert(camaraClick)
    }

    @Test
    fun cuando_hay_foto_se_muestra_imagen_sino_texto() {
        // Caso 1: Sin foto
        composeTestRule.setContent {
            ProfileContent(
                fotoUri = "",
                nombre = "",
                fono = "",
                onBack = {}, onNameChange = {}, onFonoChange = {}, onSaveClick = {}, onGalleryClick = {}, onCameraClick = {}
            )
        }

        composeTestRule
            .onNodeWithTag(ProfileTags.PHOTO_PREVIEW)
            .assertIsDisplayed()
            .assertTextContains("Sin foto")
    }
}