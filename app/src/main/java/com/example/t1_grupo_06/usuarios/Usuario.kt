package com.example.t1_grupo_06.usuarios

data class Usuario(
    val usuario: String,
    val nombre: String,
    val correo: String,
    val contrasenaHash: String
)
