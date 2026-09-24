package com.example.t1_grupo_06

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TwoFactorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_factor)

        // Recibir el código OTP generado en MainActivity
        val codigoEsperado = intent.getStringExtra("CODIGO_OTP") ?: ""

        val etOtpCode = findViewById<EditText>(R.id.etOtpCode)
        val btnVerificarOtp = findViewById<Button>(R.id.btnVerificarOtp)

        btnVerificarOtp.setOnClickListener {
            val codigoIngresado = etOtpCode.text.toString().trim()

            // 1. Validar que no esté vacío
            if (codigoIngresado.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa el código OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Comparar el código ingresado con el código enviado en la notificación
            if (codigoIngresado == codigoEsperado) {
                Toast.makeText(this, "✅ Autenticación exitosa", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProductosActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Bloquear el acceso y mostrar mensaje informativo
                Toast.makeText(this, "Código OTP incorrecto. Verifica tu notificación.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
