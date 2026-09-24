package com.example.t1_grupo_06

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    // ID del canal de notificaciones (requerido desde Android 8+)
    private val CANAL_ID = "canal_2fa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crear el canal de notificaciones al iniciar la app
        crearCanalNotificacion()

        // Enlazamos componentes
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnCrearUsuario = findViewById<Button>(R.id.btnCrearUsuario)
        val btnEnviar = findViewById<Button>(R.id.btnIniciarSesion)

        // Crear usuario
        btnCrearUsuario.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        // Login
        btnEnviar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            // Validación de campos vacíos
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de credenciales para dar acceso
            if (usuario == "admin" && contrasena == "admin123") {

                // 1. Generar código OTP aleatorio de 6 dígitos
                val codigoOtp = (100000..999999).random().toString()

                // 2. Enviar el código como notificación (simulando un correo)
                enviarNotificacionOtp(codigoOtp)

                Toast.makeText(this, "Se ha enviado un código de verificación", Toast.LENGTH_SHORT).show()

                // 3. Pasar el código generado a la pantalla de 2FA
                val intent = Intent(this, TwoFactorActivity::class.java)
                intent.putExtra("CODIGO_OTP", codigoOtp)
                startActivity(intent)

            } else {
                // Mensaje informativo si las credenciales fallan
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Crea el canal de notificaciones (obligatorio para Android 8+).
     * Este canal se puede reutilizar para futuras funcionalidades
     * como el envío de códigos al registrar un nuevo usuario.
     */
    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Verificación 2FA"
            val descripcion = "Canal para enviar códigos de verificación de dos factores"
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val canal = NotificationChannel(CANAL_ID, nombre, importancia).apply {
                description = descripcion
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    /**
     * Construye y lanza una notificación del sistema simulando
     * la llegada de un correo electrónico con el código OTP.
     *
     * @param codigo El código OTP de 6 dígitos generado aleatoriamente.
     *
     * NOTA PARA EL EQUIPO: Esta función se puede reutilizar fácilmente
     * desde RegistroActivity cuando se implemente el registro con correo.
     * Solo hay que llamar: enviarNotificacionOtp(codigoGenerado)
     */
    private fun enviarNotificacionOtp(codigo: String) {
        val notificacion = NotificationCompat.Builder(this, CANAL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Icono de la app
            .setContentTitle("📩 Nuevo correo: Verificación 2FA")
            .setContentText("Tu código de acceso es: $codigo")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Hola, has solicitado iniciar sesión en T1 Grupo 06.\n\n" +
                        "🔐 Tu código de verificación es: $codigo\n\n" +
                        "Si no fuiste tú, ignora este mensaje."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Se elimina al tocarla
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, notificacion)
    }
}