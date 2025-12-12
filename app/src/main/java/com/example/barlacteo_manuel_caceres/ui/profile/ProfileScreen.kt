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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.barlacteo_manuel_caceres.data.local.AccountRepository
import com.example.barlacteo_manuel_caceres.data.repository.ProfileRepository
import com.example.barlacteo_manuel_caceres.domain.model.PedidoUsuario
import com.example.barlacteo_manuel_caceres.utils.ImageUtils
object ProfileTags {
    const val PHOTO_PREVIEW = "photo_preview"
    const val BTN_GALLERY = "btn_gallery"
    const val BTN_CAMERA = "btn_camera"
    const val INPUT_NAME = "input_name"
    const val INPUT_PHONE = "input_phone"
    const val BTN_SAVE = "btn_save"
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Repositorios
    val profileRepo = remember { ProfileRepository() }
    val accountRepo = remember { AccountRepository(context) } // Necesario para obtener el fono actual

    val vm: ProfileViewModel = viewModel(
        factory = ProfileVMFactory(profileRepo, context)
    )

    val state by vm.state.collectAsState()
    val historial by vm.historial.collectAsState()
    val currentAccount by accountRepo.currentAccountFlow.collectAsState(initial = null)

    LaunchedEffect(currentAccount) {
        currentAccount?.let {
            vm.prefill(it.nombre, it.fono)
            //Cargamos el historial apenas se identifica al usuario
            vm.cargarHistorial(it.fono)
        }
    }

    // Lógica de cámara
    val pickPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            // Comprimimos antes de llamar al VM
            val archivoComprimido = ImageUtils.comprimirImagen(context, it)
            vm.subirFoto(archivoComprimido)
        }
    }

    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { ok ->
        if (ok && cameraUri != null) {
            val archivoComprimido = ImageUtils.comprimirImagen(context, cameraUri!!)
            vm.subirFoto(archivoComprimido) // Usamos la versión comprimida
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

    // UI
    ProfileContent(
        fotoUri = state.fotoUri,
        nombre = state.nombre,
        fono = state.fono,
        historial = historial,
        onBack = onBack,
        onNameChange = vm::updateNombre,
        onFonoChange = vm::updateFono,
        onSaveClick = vm::save,
        onGalleryClick = { pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    fotoUri: String,
    nombre: String,
    fono: String,
    historial: List<PedidoUsuario>,
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
        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Foto
                    Crossfade(targetState = fotoUri, label = "photoFade") { uri ->
                        if (uri.isNotBlank()) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Foto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .testTag(ProfileTags.PHOTO_PREVIEW)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .testTag(ProfileTags.PHOTO_PREVIEW),
                                contentAlignment = Alignment.Center
                            ) { Text("Sin foto") }
                        }
                    }

                    // Botones
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onGalleryClick,
                            modifier = Modifier.weight(1f).testTag(ProfileTags.BTN_GALLERY)
                        ) { Text("Galería") }
                        OutlinedButton(
                            onClick = onCameraClick,
                            modifier = Modifier.weight(1f).testTag(ProfileTags.BTN_CAMERA)
                        ) { Text("Cámara") }
                    }

                    // Campos
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = onNameChange,
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag(ProfileTags.INPUT_NAME)
                    )
                    OutlinedTextField(
                        value = fono,
                        onValueChange = onFonoChange,
                        label = { Text("Celular") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag(ProfileTags.INPUT_PHONE)
                    )

                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.fillMaxWidth().testTag(ProfileTags.BTN_SAVE)
                    ) { Text("Guardar Perfil") }

                    Spacer(Modifier.height(20.dp))
                    Text("Mis Pedidos", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider() // Línea separadora
                }
            }

            if (historial.isEmpty()) {
                item {
                    Text(
                        "No tienes pedidos registrados.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            } else {
                items(historial) { pedido ->
                    PedidoItem(pedido)
                }
            }
        }
    }
}

@Composable
fun PedidoItem(pedido: PedidoUsuario) {
    val colorEstado = if (pedido.estado == "PAGADO") Color(0xFF4CAF50) else Color(0xFFFF9800)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Pedido #${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Fecha: ${pedido.fecha?.take(10) ?: "Reciente"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${pedido.total}", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = pedido.estado,
                    color = colorEstado,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
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