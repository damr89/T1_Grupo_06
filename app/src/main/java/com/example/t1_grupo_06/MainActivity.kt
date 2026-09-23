package com.example.t1_grupo_06

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enlazamos botones
        val btnCrearUsuario = findViewById<Button>(R.id.btnCrearUsuario)
        val btnEnviar = findViewById<Button>(R.id.btnIniciarSesion)

        // Crear usuario
        btnCrearUsuario.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        // Login
        btnEnviar.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }
    }
}