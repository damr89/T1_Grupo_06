package com.example.t1_grupo_06.usuarios

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
class UsuarioDbHelper(context: Context) :
    SQLiteOpenHelper(context, NOMBRE_BD, null, VERSION_BD) {

    companion object {
        private const val NOMBRE_BD = "usuarios.db"
        private const val VERSION_BD = 1

        const val TABLA = "usuarios"
        const val COL_USUARIO = "usuario"
        const val COL_NOMBRE = "nombre"
        const val COL_CORREO = "correo"
        const val COL_CONTRASENA = "contrasena_hash"
        fun hashContrasena(contrasena: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(contrasena.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLA (
                $COL_USUARIO TEXT PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL,
                $COL_CONTRASENA TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Usuario administrador por defecto, para no romper el acceso que ya
        // existía con admin / admin123 antes de conectar el Login a la BD.
        val valoresAdmin = ContentValues().apply {
            put(COL_USUARIO, "admin")
            put(COL_NOMBRE, "Administrador")
            put(COL_CORREO, "admin@upn.edu.pe")
            put(COL_CONTRASENA, hashContrasena("admin123"))
        }
        db.insert(TABLA, null, valoresAdmin)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA")
        onCreate(db)
    }

    //ESCRITURA (Registrar)
    fun insertarUsuario(usuario: Usuario): Boolean {
        val valores = ContentValues().apply {
            put(COL_USUARIO, usuario.usuario)
            put(COL_NOMBRE, usuario.nombre)
            put(COL_CORREO, usuario.correo)
            put(COL_CONTRASENA, usuario.contrasenaHash)
        }
        return writableDatabase.insert(TABLA, null, valores) != -1L
    }

    //  LECTURA / VALIDACIÓN (Login)
    fun existeUsuario(usuario: String): Boolean = buscarPorUsuario(usuario) != null

    fun existeCorreo(correo: String): Boolean {
        return readableDatabase.query(
            TABLA, null, "$COL_CORREO = ?", arrayOf(correo), null, null, null
        ).use { it.moveToFirst() }
    }

    fun buscarPorUsuario(usuario: String): Usuario? {
        return readableDatabase.query(
            TABLA, null, "$COL_USUARIO = ?", arrayOf(usuario), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.aUsuario() else null
        }
    }

    fun validarCredenciales(usuario: String, contrasena: String): Boolean {
        val usuarioGuardado = buscarPorUsuario(usuario) ?: return false
        return usuarioGuardado.contrasenaHash == hashContrasena(contrasena)
    }

    // Convierte la fila actual del cursor en un objeto Usuario
    private fun Cursor.aUsuario() = Usuario(
        usuario = getString(getColumnIndexOrThrow(COL_USUARIO)),
        nombre = getString(getColumnIndexOrThrow(COL_NOMBRE)),
        correo = getString(getColumnIndexOrThrow(COL_CORREO)),
        contrasenaHash = getString(getColumnIndexOrThrow(COL_CONTRASENA))
    )
}
