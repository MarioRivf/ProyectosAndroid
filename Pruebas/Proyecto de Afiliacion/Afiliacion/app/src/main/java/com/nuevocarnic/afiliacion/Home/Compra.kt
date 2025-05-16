package com.nuevocarnic.afiliacion.Home

data class Compra(
    val id: Int,
    val nombreCliente: String,
    val ubicacion: String,
    val fechaHora: String, // ISO-8601 del backend. Usás String o LocalDateTime si lo parseás.
    val codigoDescuento: String? = null
)