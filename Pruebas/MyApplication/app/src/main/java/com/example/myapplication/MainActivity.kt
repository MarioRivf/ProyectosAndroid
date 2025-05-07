package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.location.Location
import androidx.compose.material3.Scaffold
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                LocationScreen()
            }
        }
    }

    @Composable
    fun LocationScreen() {
        var locationText by remember { mutableStateOf("Esperando ubicación...") }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = locationText)

            Button(onClick = {
                getCurrentLocation { lat, lon ->
                    locationText = "Latitud: $lat\nLongitud: $lon"
                }
            }) {
                Text("Obtener Ubicación")
            }
        }
    }

    private fun getCurrentLocation(onLocationReceived: (Double, Double) -> Unit) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                onLocationReceived(lat, lon)
            } else {
                onLocationReceived(0.0, 0.0)
            }
        }
    }

    // Aquí, manejamos los resultados de los permisos manualmente
    fun handlePermissionResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation { lat, lon ->
                Toast.makeText(this, "Ubicación: $lat, $lon", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
