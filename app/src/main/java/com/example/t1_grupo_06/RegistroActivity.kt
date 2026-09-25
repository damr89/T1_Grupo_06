package com.example.t1_grupo_06

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.t1_grupo_06.usuarios.Usuario
import com.example.t1_grupo_06.usuarios.UsuarioDbHelper

class RegistroActivity : AppCompatActivity() {
    private lateinit var dbHelper: UsuarioDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        dbHelper = UsuarioDbHelper(this)

        // Vincular los elementos visuales del registro usando los IDs exactos
        val etNombre = findViewById<EditText>(R.id.etNombreRegistro)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etUsuario = findViewById<EditText>(R.id.etUsuarioRegistro)
        val etContrasena = findViewById<EditText>(R.id.etContrasenaRegistro)
        val etConfirmarContrasena = findViewById<EditText>(R.id.etConfirmarContrasena)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnVolverLogin = findViewById<Button>(R.id.btnVolverLogin)

        // Programar la acción del botón "Registrar" / "Crear usuario"
        btnRegistrar.setOnClickListener {
            registrarUsuario(etNombre, etCorreo, etUsuario, etContrasena, etConfirmarContrasena)
        }

        // Acción de regresar al Login manualmente si el usuario lo desea
        btnVolverLogin.setOnClickListener {
            irALogin()
        }
    }
    private fun registrarUsuario(
        etNombre: EditText,
        etCorreo: EditText,
        etUsuario: EditText,
        etContrasena: EditText,
        etConfirmarContrasena: EditText
    ) {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val usuario = etUsuario.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()
        val confirmar = etConfirmarContrasena.text.toString().trim()

        // 1. Ningún campo debe estar vacío
        if (nombre.isEmpty() || correo.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. El correo debe tener un formato válido
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Ingresa un correo electrónico válido"
            etCorreo.requestFocus()
            return
        }

        // 3. El usuario debe tener una longitud mínima y sin espacios
        if (usuario.length < 4 || usuario.contains(" ")) {
            etUsuario.error = "El usuario debe tener al menos 4 caracteres, sin espacios"
            etUsuario.requestFocus()
            return
        }

        // 4. La contraseña debe cumplir una longitud mínima
        if (contrasena.length < 6) {
            etContrasena.error = "La contraseña debe tener al menos 6 caracteres"
            etContrasena.requestFocus()
            return
        }

        // 5. Las contraseñas deben coincidir
        if (contrasena != confirmar) {
            etConfirmarContrasena.error = "Las contraseñas no coinciden"
            etConfirmarContrasena.requestFocus()
            return
        }

        // 6. El usuario no debe existir ya en la base de datos
        if (dbHelper.existeUsuario(usuario)) {
            etUsuario.error = "Ese nombre de usuario ya está registrado"
            etUsuario.requestFocus()
            return
        }

        // 7. El correo no debe estar registrado por otra cuenta
        if (dbHelper.existeCorreo(correo)) {
            etCorreo.error = "Ese correo ya está registrado"
            etCorreo.requestFocus()
            return
        }

        // 8. Todo validado: guardar el usuario con la contraseña encriptada (SHA-256)
        val nuevoUsuario = Usuario(
            usuario = usuario,
            nombre = nombre,
            correo = correo,
            contrasenaHash = UsuarioDbHelper.hashContrasena(contrasena)
        )
        val guardadoOk = dbHelper.insertarUsuario(nuevoUsuario)

        if (guardadoOk) {
            Toast.makeText(this, "¡Usuario registrado con éxito!", Toast.LENGTH_LONG).show()
            // Redirigir de vuelta al Login, pre-llenando el usuario recién creado
            irALogin(usuario)
        } else {
            Toast.makeText(this, "No se pudo registrar el usuario. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun irALogin(usuarioRegistrado: String? = null) {
        val intent = Intent(this, MainActivity::class.java)
        usuarioRegistrado?.let { intent.putExtra("USUARIO_REGISTRADO", it) }
        startActivity(intent)
        finish()
    }
}
