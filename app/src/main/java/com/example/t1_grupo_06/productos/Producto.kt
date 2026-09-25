package com.example.t1_grupo_06.productos

import java.util.Locale

/**
 * Modelo de datos de un producto del inventario.
 * El código es único y funciona como clave primaria en la base de datos.
 */
data class Producto(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val marca: String
) {
    // Precio en soles con 2 decimales, ej: "S/ 12.50"
    fun precioFormateado(): String = "S/ %.2f".format(Locale.US, precio)
}
