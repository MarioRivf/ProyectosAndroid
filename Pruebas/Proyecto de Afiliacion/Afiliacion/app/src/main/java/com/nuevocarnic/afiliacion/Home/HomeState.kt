package com.nuevocarnic.afiliacion.Home

data class HomeState(
    val compras: List<Compra> = emptyList(),
    val nombreCliente: String = "",
    val ubicacion: String = "",
    val fechaHora: String = "",
    val codigoDescuento: String? = null,
    val compraId: Int? = null
)
