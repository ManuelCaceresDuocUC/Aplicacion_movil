package com.example.barlacteo_manuel_caceres.ui.profile

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository

// 1. Definimos los TestTags
object ProfileTags {
    const val PHOTO_PREVIEW = "photo_preview"
    const val BTN_GALLERY = "btn_gallery"
    const val BTN_CAMERA = "btn_camera"
    const val INPUT_NAME = "input_name"
    const val INPUT_PHONE = "input_phone"
    const val BTN_SAVE = "btn_save"
}

/**
 * Pantalla inteligente (Stateful): Maneja VM, permisos y cámara.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val accountRepo = remember { AccountRepository(context) }
    val profileRepo = remember { ProfileRepository(context) }

    val vm: ProfileViewModel = viewModel(
        factory = ProfileVMFactory(profileRepo, accountRepo)
    )
    val state by vm.state.collectAsState()

    // Lógica de prellenado
    val currentAccount by accountRepo.currentAccountFlow.collectAsState(initial = null)
    LaunchedEffect(currentAccount) {
        currentAccount?.let { vm.prefill(it.nombre, it.fono) }
    }

    // Lógica de cámara y galería
    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { vm.updateFoto(it.toString()) } }

    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok -> if (ok && cameraUri != null) vm.updateFoto(cameraUri.toString()) }

    val requestCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = newCameraUri(context)
            cameraUri = uri
            if (uri != null) takePicture.launch(uri)
        }
    }

    // 2. Llamamos a la UI pura pasándole solo los eventos
    ProfileContent(
        fotoUri = state.fotoUri,
        nombre = state.nombre,
        fono = state.fono,
        onBack = onBack,
        onNameChange = vm::updateNombre,
        onFonoChange = vm::updateFono,
        onSaveClick = vm::save,
        onGalleryClick = {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCameraClick = {
            val camPerm = Manifest.permission.CAMERA
            if (ContextCompat.checkSelfPermission(context, camPerm) != PackageManager.PERMISSION_GRANTED) {
                requestCamera.launch(camPerm)
            } else {
                val uri = newCameraUri(context)
                cameraUri = uri
                if (uri != null) takePicture.launch(uri)
            }
        }
    )
}

/**
 * UI Pura (Stateless): Lista para testing.
 * No sabe nada de permisos ni de Android APIs complejas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    fotoUri: String,
    nombre: String,
    fono: String,
    onBack: () -> Unit,
    onNameChange: (String) -> Unit,
    onFonoChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Atrás") } }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Foto
            Crossfade(targetState = fotoUri, label = "photoFade") { uri ->
                if (uri.isNotBlank()) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .testTag(ProfileTags.PHOTO_PREVIEW) // TAG
                    )
                } else {
                    Text("Sin foto", modifier = Modifier.testTag(ProfileTags.PHOTO_PREVIEW))
                }
            }

            // Botones Foto
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onGalleryClick,
                    modifier = Modifier.testTag(ProfileTags.BTN_GALLERY) // TAG
                ) { Text("Elegir de galería") }

                OutlinedButton(
                    onClick = onCameraClick,
                    modifier = Modifier.testTag(ProfileTags.BTN_CAMERA) // TAG
                ) { Text("Tomar foto") }
            }

            // Campo Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProfileTags.INPUT_NAME) // TAG
            )

            // Campo Celular
            OutlinedTextField(
                value = fono,
                onValueChange = onFonoChange,
                label = { Text("Celular (+569...)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProfileTags.INPUT_PHONE) // TAG
            )

            // Botón Guardar
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProfileTags.BTN_SAVE) // TAG
            ) { Text("Guardar perfil") }
        }
    }
}

// Función auxiliar (se mantiene igual)
private fun newCameraUri(context: Context): android.net.Uri? {
    return try {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "perfil_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BarLacteo")
            }
        }
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    } catch (_: Exception) { null }
}