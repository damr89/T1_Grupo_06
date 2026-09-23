package com.example.t1_grupo_06

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro) // Conecta con tu diseño XML

        // Ubicamos el botón de volver
        val btnVolverLogin = findViewById<Button>(R.id.btnVolverLogin)

        // Acción de regresar al Login
        btnVolverLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}