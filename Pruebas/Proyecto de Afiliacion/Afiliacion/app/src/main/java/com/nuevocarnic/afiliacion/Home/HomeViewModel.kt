package com.nuevocarnic.afiliacion.Home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(
    private val comprasService: ComprasService
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        obtenerCompras()
    }

    fun obtenerCompras() {
        val service = ApiClient.comprasService

        viewModelScope.launch {
            try {
                val compras = service.obtenerCompras()
                state = state.copy(compras = compras)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener compras", e)
            }
        }

    }

    fun cambiarNombreCliente(valor: String) {
        state = state.copy(nombreCliente = valor)
    }

    fun cambiarUbicacion(valor: String) {
        state = state.copy(ubicacion = valor)
    }

    fun cambiarCodigoDescuento(valor: String?) {
        state = state.copy(codigoDescuento = valor)
    }

    fun eliminarCompra(compra: Compra) {
        viewModelScope.launch {
            try {
                comprasService.eliminarCompra(compra.id)
                obtenerCompras()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al eliminar compra", e)
            }
        }
    }

    fun editarCompra(compra: Compra) {
        state = state.copy(
            nombreCliente = compra.nombreCliente,
            ubicacion = compra.ubicacion,
            codigoDescuento = compra.codigoDescuento,
            compraId = compra.id
        )
    }

    fun guardarCompra() {
        val nombreCliente = state.nombreCliente.trim()
        val ubicacion = state.ubicacion.trim()
        val codigoDescuento = state.codigoDescuento?.trim()

        if (nombreCliente.isBlank() || ubicacion.isBlank()) {
            Log.w("HomeViewModel", "Datos inválidos: nombre o ubicación vacíos")
            return
        }

        val nuevaCompra = Compra(
            id = state.compraId ?: 0, // Si compraId es nulo, asigna 0
            nombreCliente = nombreCliente,
            ubicacion = ubicacion,
            fechaHora = "2025-05-08T14:30:00",
            codigoDescuento = codigoDescuento
        )

        viewModelScope.launch {
            try {
                if (state.compraId == null) {
                    comprasService.agregarCompra(nuevaCompra)
                } else {
                    comprasService.actualizarCompra(nuevaCompra.id, nuevaCompra)
                }
                obtenerCompras()
                limpiarFormulario()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al guardar compra", e)
            }
        }
    }

    fun limpiarFormulario() {
        state = state.copy(
            nombreCliente = "",
            ubicacion = "",
            codigoDescuento = null,
            compraId = null
        )
    }
}
