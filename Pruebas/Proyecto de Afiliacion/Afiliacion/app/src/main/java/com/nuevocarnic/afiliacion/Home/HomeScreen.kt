package com.nuevocarnic.afiliacion.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    ubicacionViewModel: UbicacionViewModel
) {
    val state = viewModel.state

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val ubicacionActual by ubicacionViewModel.ubicacion.collectAsState()
        Text("Mi ubicación es: $ubicacionActual")

        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(state.compras) { compra ->
                CompraItem(
                    compra = compra,
                    onDelete = { viewModel.eliminarCompra(compra) },
                    onEdit = { viewModel.editarCompra(compra) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = state.nombreCliente,
            onValueChange = { viewModel.cambiarNombreCliente(it) },
            label = { Text("Nombre del Cliente") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        TextField(
            value = state.ubicacion,
            onValueChange = { viewModel.cambiarUbicacion(it) },
            label = { Text("Ubicación") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Botón para guardar
        Button(
            onClick = {
                viewModel.cambiarUbicacion(ubicacionActual)
                viewModel.guardarCompra()
            },
            modifier = Modifier.align(Alignment.End).padding(top = 8.dp)
        ) {
            Text("Guardar Compra")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.obtenerCompras()
    }
}

@Composable
fun CompraItem(compra: Compra, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Cliente: ${compra.nombreCliente}", style = MaterialTheme.typography.bodyMedium)
            Text("Ubicación: ${compra.ubicacion}", style = MaterialTheme.typography.bodySmall)
            Text("Fecha: ${compra.fechaHora}", style = MaterialTheme.typography.bodySmall)

            compra.codigoDescuento?.let {
                Text("Código de Descuento: $it", style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Editar")
                }
                TextButton(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}
