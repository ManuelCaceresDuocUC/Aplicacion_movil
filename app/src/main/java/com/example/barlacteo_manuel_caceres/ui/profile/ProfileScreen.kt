package com.example.barlacteo_manuel_caceres.ui.profile

// Repos locales
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository

// Android base
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore

// Activity Result APIs para intents de galería/cámara/permisos
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

// Compose UI
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Util permisos
import androidx.core.content.ContextCompat

// VM
import androidx.lifecycle.viewmodel.compose.viewModel

// Imagen asíncrona
import coil.compose.AsyncImage

// Perfil repo
import com.example.barlacteo_manuel_caceres.data.local.ProfileRepository

// Animación para transición suave de foto
import androidx.compose.animation.Crossfade

/**
 * Pantalla de Perfil.
 *
 * Flujo:
 * 1) Prefill desde AccountRepository si hay cuenta actual.
 * 2) Elegir foto desde galería o tomar con cámara. Se guarda Uri en el estado del VM.
 * 3) Editar nombre y celular.
 * 4) Guardar con `vm.save()`.
 *
 * @param onBack callback para volver atrás.
 */
@OptIn(ExperimentalMaterial3Api::class)
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

    val currentAccount by accountRepo.currentAccountFlow.collectAsState(initial = null)
    LaunchedEffect(currentAccount) {
        currentAccount?.let { vm.prefill(it.nombre, it.fono) }
    }

    // ---------- Selectores de imagen ----------

    // Galería:
    // PickVisualMedia delega el acceso al sistema de fotos.
    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Si el usuario eligió una foto, guardamos su Uri en el estado.
        uri?.let { vm.updateFoto(it.toString()) }
    }

    // Cámara: necesitamos un Uri de destino donde se escribirá la foto.
    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    // Lanza la cámara para capturar imagen en el Uri entregado.
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        // Si el usuario efectivamente tomó la foto, persistimos el Uri.
        if (ok && cameraUri != null) {
            vm.updateFoto(cameraUri.toString())
        }
    }

    // Pedido de permiso de cámara en tiempo de ejecución.
    val requestCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Creamos un Uri de salida y lanzamos la cámara.
            val uri = newCameraUri(context)
            cameraUri = uri
            if (uri != null) takePicture.launch(uri)
        }
        // Si no se concede, no hacemos nada. La UI queda igual.
    }

    // ---------- UI scaffold ----------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Atrás") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)      // Evita solaparse con la app bar
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Foto con transición suave entre estados. Si no hay foto, muestra texto.
            Crossfade(targetState = state.fotoUri) { uri ->
                if (uri.isNotBlank()) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                } else {
                    Text("Sin foto")
                }
            }

            // Acciones de imagen: galería o cámara.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        // Abre selector solo de imágenes.
                        pickPhoto.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) { Text("Elegir de galería") }

                OutlinedButton(
                    onClick = {
                        // Verifica permiso de cámara. Si no está concedido, lo solicita.
                        val camPerm = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, camPerm)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestCamera.launch(camPerm)
                        } else {
                            // Permiso ya concedido. Creamos Uri y lanzamos cámara.
                            val uri = newCameraUri(context)
                            cameraUri = uri
                            if (uri != null) takePicture.launch(uri)
                        }
                    }
                ) { Text("Tomar foto") }
            }

            // Campo: Nombre
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::updateNombre, // Delegamos validación/normalización al VM.
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Celular, teclado tipo teléfono
            OutlinedTextField(
                value = state.fono,
                onValueChange = vm::updateFono,
                label = { Text("Celular (+569...)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Guardar cambios en repositorio a través del VM.
            Button(
                onClick = { vm.save() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar perfil") }
        }
    }
}

/**
 * Crea un Uri en MediaStore para guardar una nueva foto de la cámara.
 * - Devuelve null si la inserción falla.
 */
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
