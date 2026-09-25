package com.example.t1_grupo_06.productos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Base de datos local (SQLite) del módulo de Productos.
 * Los productos quedan guardados en el dispositivo aunque se cierre la app.
 */
class ProductoDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "productos.db"
        private const val VERSION_BD = 1

        const val TABLA = "productos"
        const val COL_CODIGO = "codigo"
        const val COL_NOMBRE = "nombre"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_PRECIO = "precio"
        const val COL_MARCA = "marca"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA (
                $COL_CODIGO TEXT PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DESCRIPCION TEXT NOT NULL,
                $COL_PRECIO REAL NOT NULL,
                $COL_MARCA TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    // ==================== ESCRITURA (Registrar) ====================

    /**
     * Inserta un producto nuevo.
     * @return true si se guardó, false si falló (por ejemplo, código repetido).
     */
    fun insertarProducto(producto: Producto): Boolean {
        val valores = ContentValues().apply {
            put(COL_CODIGO, producto.codigo)
            put(COL_NOMBRE, producto.nombre)
            put(COL_DESCRIPCION, producto.descripcion)
            put(COL_PRECIO, producto.precio)
            put(COL_MARCA, producto.marca)
        }
        return writableDatabase.insert(TABLA, null, valores) != -1L
    }

    // ==================== LECTURA (Buscar / Listar) ====================

    fun existeProducto(codigo: String): Boolean = buscarPorCodigo(codigo) != null

    /** Busca un producto por su código exacto. Devuelve null si no existe. */
    fun buscarPorCodigo(codigo: String): Producto? {
        return readableDatabase.query(
            TABLA, null, "$COL_CODIGO = ?", arrayOf(codigo), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.aProducto() else null
        }
    }

    /**
     * Lista los productos ordenados por nombre.
     * Si se envía un filtro, solo devuelve los que coinciden por código, nombre o marca.
     */
    fun listarProductos(filtro: String = ""): List<Producto> {
        var seleccion: String? = null
        var argumentos: Array<String>? = null
        if (filtro.isNotBlank()) {
            val patron = "%${filtro.trim()}%"
            seleccion = "$COL_CODIGO LIKE ? OR $COL_NOMBRE LIKE ? OR $COL_MARCA LIKE ?"
            argumentos = arrayOf(patron, patron, patron)
        }

        return readableDatabase.query(
            TABLA, null, seleccion, argumentos, null, null, "$COL_NOMBRE COLLATE NOCASE ASC"
        ).use { cursor ->
            val productos = mutableListOf<Producto>()
            while (cursor.moveToNext()) {
                productos.add(cursor.aProducto())
            }
            productos
        }
    }

    // Convierte la fila actual del cursor en un objeto Producto
    private fun Cursor.aProducto() = Producto(
        codigo = getString(getColumnIndexOrThrow(COL_CODIGO)),
        nombre = getString(getColumnIndexOrThrow(COL_NOMBRE)),
        descripcion = getString(getColumnIndexOrThrow(COL_DESCRIPCION)),
        precio = getDouble(getColumnIndexOrThrow(COL_PRECIO)),
        marca = getString(getColumnIndexOrThrow(COL_MARCA))
    )
}
