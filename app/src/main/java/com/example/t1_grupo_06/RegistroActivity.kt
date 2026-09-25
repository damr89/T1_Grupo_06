package com.example.t1_grupo_06

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro) 

        // Vincular los elementos visuales del registro usando los IDs exactos
        val etNombre = findViewById<EditText>(R.id.etNombreRegistro)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etUsuario = findViewById<EditText>(R.id.etUsuarioRegistro)
        val etContrasena = findViewById<EditText>(R.id.etContrasenaRegistro)
        val etConfirmarContrasena = findViewById<EditText>(R.id.etConfirmarContrasena)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Programar la acción del botón "Registrar" / "Crear usuario"
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val confirmar = etConfirmarContrasena.text.toString().trim()

            // Ningún campo debe estar vacío
            if (nombre.isEmpty() || correo.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Las contraseñas deben coincidir
            if (contrasena != confirmar) {
                etConfirmarContrasena.error = "Las contraseñas no coinciden"
                etConfirmarContrasena.requestFocus()
                return@setOnClickListener
            }

            // Guardar los datos localmente (SharedPreferences) para que el Login pueda autenticarlos
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("USER_NAME", nombre)
            editor.putString("USER_EMAIL", correo)
            editor.putString("USER_USER", usuario)
            editor.putString("USER_PASSWORD", contrasena)
            editor.apply()

            Toast.makeText(this, "¡Usuario registrado con éxito!", Toast.LENGTH_LONG).show()

            // Redirigir de vuelta al Login automáticamente tras registrarse con éxito
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Ubicamos el botón de volver (que ya tenías implementado)
        val btnVolverLogin = findViewById<Button>(R.id.btnVolverLogin)

        // Acción de regresar al Login manualmente si el usuario lo desea
        btnVolverLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
