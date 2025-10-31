package com.example.barlacteo_manuel_caceres.ui.profile
import com.example.barlacteo_manuel_caceres.data.AccountRepository

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.barlacteo_manuel_caceres.data.ProfileRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val vm: ProfileViewModel = viewModel(factory = ProfileVMFactory(ProfileRepository(context)))
    val state by vm.state.collectAsState()
    val accountRepo = remember { AccountRepository(context) }
    val currentAccount by accountRepo.currentAccountFlow.collectAsState(initial = null)
    LaunchedEffect(currentAccount) {
        currentAccount?.let { vm.prefill(it.nombre, it.fono) }
    }

    // Galería (no requiere permisos extra en Android 13+)
    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { vm.updateFoto(it.toString()) }
    }

    // Cámara
    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok && cameraUri != null) {
            vm.updateFoto(cameraUri.toString())
        }
    }

    val requestCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = newCameraUri(context)
            cameraUri = uri
            if (uri != null) takePicture.launch(uri)
        }
    }

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
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Foto
            if (state.fotoUri.isNotBlank()) {
                AsyncImage(
                    model = state.fotoUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
            } else {
                Text("Sin foto")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        pickPhoto.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) { Text("Elegir de galería") }

                OutlinedButton(
                    onClick = {
                        val camPerm = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, camPerm)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestCamera.launch(camPerm)
                        } else {
                            val uri = newCameraUri(context)
                            cameraUri = uri
                            if (uri != null) takePicture.launch(uri)
                        }
                    }
                ) { Text("Tomar foto") }
            }

            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::updateNombre,
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.fono,
                onValueChange = vm::updateFono,
                label = { Text("Celular (+569...)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { vm.save() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar perfil") }
        }
    }
}

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
