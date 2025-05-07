package com.nuevocarnic.afiliacion

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.gms.location.LocationServices
import com.nuevocarnic.afiliacion.ui.theme.AfiliacionTheme
import androidx.compose.runtime.*
import android.Manifest
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import com.google.android.gms.location.*



class MainActivity : ComponentActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Pedir permiso si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
        setContent {
            //App()
            //PantallaConComponentes()
            //AfiliacionCliente()
            PantallaPrincipal()
        }
    }
}

@Composable
fun PantallaPrincipal() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Text(
                "Sistema de Afiliación",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                CardUbicacion()
                Spacer(modifier = Modifier.padding(8.dp))
                CardAfiliacion()
                Spacer(modifier = Modifier.padding(8.dp))
                CardFormulario()
            }
        }
    )
}

@Composable
fun CardUbicacion() {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Obteniendo ubicación...") }

    LaunchedEffect(true) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000L
            ).apply {
                setMaxUpdates(1)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        locationText = "📍 Lat: ${location.latitude}, Lng: ${location.longitude}"
                    } ?: run {
                        locationText = "Ubicación no disponible"
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            locationText = "Permiso de ubicación no concedido"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ubicación actual", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(locationText)
        }
    }
}

@Composable
fun CardAfiliacion() {
    var compras by remember { mutableStateOf(0) }
    var codigoDescuento by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Afiliación de Cliente", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Compras realizadas: $compras")
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                compras++
                codigoDescuento = if (compras % 5 == 0) generarCodigo() else null
            }) {
                Text("Registrar compra")
            }

            codigoDescuento?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text("🎉 Código de descuento: $it", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun CardFormulario() {
    var texto by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Formulario", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Escribe algo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* lógica aquí */ }) {
                Text("Enviar")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Texto ingresado: $texto")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun App() {
    val context = LocalContext.current
    var locationText by remember { mutableStateOf("Obteniendo ubicación...") }

    LaunchedEffect(true) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Usa una ubicación fija para pruebas
            val location = Location("fused")
            location.latitude = 37.4219983 // Latitud de Googleplex
            location.longitude = -122.084 // Longitud de Googleplex
            locationText = "Latitud: ${location.latitude}, Longitud: ${location.longitude}"

            // O usa la solicitud de ubicación real:
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000L
            ).apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(5000L)
                setMaxUpdateDelayMillis(15000L)
                setMaxUpdates(1)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location: Location? = locationResult.lastLocation
                    if (location != null) {
                        locationText = "Latitud: ${location.latitude}, Longitud: ${location.longitude}"
                    } else {
                        locationText = "Ubicación no disponible"
                    }
                }
            }

            // Solicitar ubicación activa
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )
            }
        } else {
            locationText = "Permiso de ubicación no concedido"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        Text(text = "Hola Mario desde lo aprendido en el video de YouTube")
        Text(text = "Hola Mario, aprende esto también desde casa")
        Text(text = locationText)
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AfiliacionTheme {
        Text(text = "Mario desde el preview")
    }
}

@Composable
fun PantallaConComponentes() {
    var texto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Bienvenido, Mario", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.padding(8.dp))

        TextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Escribe algo") }
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Button(onClick = { /* Acción aquí */ }) {
            Text("Enviar")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Text(text = "Texto ingresado: $texto")
    }
}

@Composable
fun AfiliacionCliente() {
    var compras by remember { mutableStateOf(0) }
    var codigoDescuento by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Compras realizadas: $compras")

        Spacer(modifier = Modifier.padding(8.dp))

        Button(onClick = {
            compras += 1
            if (compras % 5 == 0) {
                codigoDescuento = generarCodigo()
            } else {
                codigoDescuento = null
            }
        }) {
            Text("Registrar compra")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        if (codigoDescuento != null) {
            Text("🎉 ¡Felicidades! Tu código de descuento es: $codigoDescuento")
        }
    }
}

fun generarCodigo(): String {
    val letras = ('A'..'Z').toList()
    val numeros = (100..999).random()
    return "${letras.random()}${letras.random()}$numeros"
}

