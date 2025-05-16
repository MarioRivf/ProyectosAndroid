package com.nuevocarnic.afiliacion.Home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UbicacionViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Configuración de actualización de ubicación (10 seg intervalo)
    private val locationRequest = LocationRequest.create().apply {
        interval = 10_000
        fastestInterval = 5_000
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    private val _ubicacion = MutableStateFlow("Cargando ubicación...")
    val ubicacion = _ubicacion.asStateFlow()//.toString()

    // Coordenadas de la tienda para verificar cercanía
    //private val latTienda = 12.145133501766074
    //private val lonTienda = -86.22949296810899
    private val tiendas = listOf(
        Pair(12.154083236109578, -86.17510584295523),
        Pair(12.14513636980411, -86.22948933168789),
        Pair(12.144131266098086, -86.23588984295623) // Agregá más tiendas según necesités
    )


    private var notificadoCerca = false

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @SuppressLint("MissingPermission")
    fun iniciarActualizacionUbicacion(homeViewModel: HomeViewModel) {
        val locationCallbackWithViewModel = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                location?.let {
                    val lat = it.latitude
                    val lon = it.longitude
                    _ubicacion.value = "Lat: $lat, Lon: $lon"
                    verificarCercania(lat, lon, homeViewModel)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallbackWithViewModel, null)
    }


    init {
        crearCanalNotificacion()
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "canal_ubicacion",
                "Ubicación",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre ubicación"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun verificarCercania(lat: Double, lon: Double, homeViewModel: HomeViewModel) {
        var tiendaCercana: Pair<Double, Double>? = null
        var distanciaMinima = Float.MAX_VALUE

        for ((latTienda, lonTienda) in tiendas) {
            val dist = FloatArray(1)
            Location.distanceBetween(lat, lon, latTienda, lonTienda, dist)
            val distancia = dist[0]
            Log.d("Ubicacion", "Distancia a tienda ($latTienda,$lonTienda): $distancia m")

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia
                tiendaCercana = Pair(latTienda, lonTienda)
            }
        }

        if (distanciaMinima < 100 && !notificadoCerca) {
            notificadoCerca = true
            mostrarNotificacion("¡Estás cerca de una tienda afiliada!")
            enviarCompra("Cliente Cerca", homeViewModel)
        } else if (distanciaMinima >= 100 && notificadoCerca) {
            notificadoCerca = false
            mostrarNotificacion("¡Te alejaste de la tienda afiliada!")
            //enviarCompra("Cliente Lejos", homeViewModel)
        }
    }


    private fun mostrarNotificacion(mensaje: String) {
        val builder = NotificationCompat.Builder(context, "canal_ubicacion")
            .setSmallIcon(android.R.drawable.ic_dialog_map) // Cambia por tu icono
            .setContentTitle("Afiliación")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val nm = ContextCompat.getSystemService(context, NotificationManager::class.java)
        nm?.notify(1001, builder.build())
    }

    private fun enviarCompra(nombre: String, viewModel: HomeViewModel) {
        viewModel.cambiarNombreCliente(nombre)
        viewModel.cambiarUbicacion(_ubicacion.value)
        viewModel.guardarCompra()
        viewModel.obtenerCompras()
        viewModel.limpiarFormulario()
    }
}
