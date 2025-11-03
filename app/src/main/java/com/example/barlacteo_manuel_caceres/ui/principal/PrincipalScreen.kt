package com.example.barlacteo_manuel_caceres.ui.principal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.barlacteo_manuel_caceres.domain.model.Oferta
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SiguienteScreen(
    nombre: String,
    fono: String,
    onBack: () -> Unit,
    ofertas: List<Oferta> = emptyList(),            // <— pásalas desde Nav o usa dummy
    onClickOferta: (Oferta) -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { ofertas.size.coerceAtLeast(1) })

    // Autodeslizamiento cada 4 s
    LaunchedEffect(ofertas.size) {
        if (ofertas.isNotEmpty()) {
            while (true) {
                delay(4000)
                val next = (pagerState.currentPage + 1) % ofertas.size
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Scaffold { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("Hola, $nombre", style = MaterialTheme.typography.headlineSmall)
            Text("Tu teléfono: $fono", style = MaterialTheme.typography.bodyLarge)

            // === CARRUSEL AQUÍ ===
            if (ofertas.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) { page ->
                    val o = ofertas[page]
                    ElevatedCard(
                        onClick = { onClickOferta(o) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column {
                            AsyncImage(
                                model = o.imagenUrl,
                                contentDescription = o.titulo,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            )
                            Column(Modifier.padding(12.dp)) {
                                Text(o.titulo, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    o.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                }

                // Indicadores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(ofertas.size) { i ->
                        val selected = i == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 6.dp)
                                .height(8.dp)
                                .width(if (selected) 18.dp else 8.dp)
                                .background(
                                    if (selected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            // Botón volver
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Volver") }
        }
    }

}
@Preview(showBackground = true)
@Composable
private fun SiguienteScreenPreview() {
    val demo = listOf(
        Oferta("1","Combo 1","Aprovecha ya !","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo1.jpg"),
        Oferta("2","Combo 2","Delicioso","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo2.jpg"),
        Oferta("3","Combo 3","Con credencial","https://barlacteo-catalogo.s3.us-east-1.amazonaws.com/Combo3.jpg")
    )
    SiguienteScreen(
        nombre = "Manuel",
        fono = "+56912345678",
        onBack = {},
        ofertas = demo,
        onClickOferta = {}
    )
}
