package com.example.t1_grupo_06

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.t1_grupo_06.productos.Producto
import com.example.t1_grupo_06.productos.ProductoAdapter
import com.example.t1_grupo_06.productos.ProductoDbHelper
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale

class ProductosActivity : AppCompatActivity() {

    companion object {
        // Código: solo letras mayúsculas, números o guiones (ej. P-001)
        private val PATRON_CODIGO = Regex("^[A-Z0-9-]+$")
    }

    // Acceso a la base de datos local de productos
    private lateinit var dbHelper: ProductoDbHelper
    private lateinit var adapter: ProductoAdapter

    // Campos del formulario
    private lateinit var tilCodigo: TextInputLayout
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilDescripcion: TextInputLayout
    private lateinit var tilPrecio: TextInputLayout
    private lateinit var tilMarca: TextInputLayout
    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etMarca: EditText

    // Listado de productos
    private lateinit var etFiltro: EditText
    private lateinit var tvTotal: TextView
    private lateinit var tvListaVacia: TextView
    private lateinit var scrollProductos: NestedScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productos)
        ajustarBordesPantalla()

        dbHelper = ProductoDbHelper(this)

        // 1. Enlazamos componentes
        tilCodigo = findViewById(R.id.tilCodigo)
        tilNombre = findViewById(R.id.tilNombre)
        tilDescripcion = findViewById(R.id.tilDescripcion)
        tilPrecio = findViewById(R.id.tilPrecio)
        tilMarca = findViewById(R.id.tilMarca)
        etCodigo = findViewById(R.id.etCodigoProducto)
        etNombre = findViewById(R.id.etNombreProducto)
        etDescripcion = findViewById(R.id.etDescripcionProducto)
        etPrecio = findViewById(R.id.etPrecioProducto)
        etMarca = findViewById(R.id.etMarcaProducto)
        etFiltro = findViewById(R.id.etFiltroProductos)
        tvTotal = findViewById(R.id.tvTotalProductos)
        tvListaVacia = findViewById(R.id.tvListaVacia)
        scrollProductos = findViewById(R.id.scrollProductos)

        // Al corregir un campo se quita su mensaje de error
        camposFormulario().forEach { til ->
            til.editText?.doAfterTextChanged { quitarError(til) }
        }

        // 2. Lista de productos: al tocar uno se muestra en el formulario
        adapter = ProductoAdapter { producto ->
            mostrarEnFormulario(producto)
            scrollProductos.smoothScrollTo(0, 0)
        }
        findViewById<RecyclerView>(R.id.rvProductos).adapter = adapter

        // Filtro en vivo mientras se escribe
        etFiltro.doAfterTextChanged { cargarProductos() }
        etFiltro.setOnEditorActionListener { _, _, _ ->
            ocultarTeclado()
            true
        }

        // 3. Botones CRUD
        findViewById<Button>(R.id.btnRegistrarProducto).setOnClickListener { registrarProducto() }
        findViewById<Button>(R.id.btnBuscarProducto).setOnClickListener { buscarProducto() }
        findViewById<Button>(R.id.btnModificarProducto).setOnClickListener { modificarProducto() }
        findViewById<Button>(R.id.btnEliminarProducto).setOnClickListener { eliminarProducto() }
        findViewById<Button>(R.id.btnLimpiarFormulario).setOnClickListener { limpiarFormulario() }

        //  Cerrar sesion
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        //  Regresar al Login
        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // 4. Mostrar los productos guardados
        cargarProductos()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    // ==================== REGISTRAR (Escritura) ====================

    private fun registrarProducto() {
        val producto = leerFormulario() ?: return

        // El código es único: no se permiten productos repetidos
        if (dbHelper.existeProducto(producto.codigo)) {
            tilCodigo.error = "Ya existe un producto con el código ${producto.codigo}"
            etCodigo.requestFocus()
            return
        }

        if (dbHelper.insertarProducto(producto)) {
            ocultarTeclado()
            limpiarFormulario()
            cargarProductos()
            Toast.makeText(this, "✅ Producto registrado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se pudo registrar el producto. Inténtalo nuevamente.", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== BUSCAR (Lectura) ====================

    private fun buscarProducto() {
        limpiarErrores()
        val codigo = codigoIngresado()

        if (codigo.isEmpty()) {
            tilCodigo.error = "Ingresa el código del producto a buscar"
            etCodigo.requestFocus()
            return
        }

        ocultarTeclado()
        val producto = dbHelper.buscarPorCodigo(codigo)

        if (producto == null) {
            // Se limpian los demás campos para no mostrar datos de otro producto
            limpiarDetalle()
            Toast.makeText(this, "No se encontró ningún producto con el código $codigo", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarEnFormulario(producto)
        Toast.makeText(this, "🔍 Producto encontrado", Toast.LENGTH_SHORT).show()
    }

    /** Lee los productos de la base de datos y actualiza la lista (aplicando el filtro, si hay). */
    private fun cargarProductos() {
        val filtro = etFiltro.text.toString().trim()
        val productos = dbHelper.listarProductos(filtro)
        adapter.submitList(productos)

        if (filtro.isEmpty()) {
            tvTotal.text = "Productos registrados (${productos.size})"
            tvListaVacia.text = "Aún no hay productos registrados."
        } else {
            tvTotal.text = "Resultados de la búsqueda (${productos.size})"
            tvListaVacia.text = "Ningún producto coincide con \"$filtro\"."
        }
        tvListaVacia.visibility = if (productos.isEmpty()) View.VISIBLE else View.GONE
    }

    // ==================== PARTE 5: MODIFICAR Y ELIMINAR ====================
    // NOTA PARA EL EQUIPO (Parte 5): reemplazar el contenido de estas dos funciones.
    //  - Modificar: val producto = leerFormulario() ?: return
    //               dbHelper.actualizarProducto(producto), luego cargarProductos()
    //  - Eliminar:  val codigo = codigoIngresado(), confirmar con un AlertDialog,
    //               dbHelper.eliminarProducto(codigo), limpiarFormulario() y cargarProductos()

    private fun modificarProducto() {
        Toast.makeText(this, "Modificar producto: en desarrollo", Toast.LENGTH_SHORT).show()
    }

    private fun eliminarProducto() {
        Toast.makeText(this, "Eliminar producto: en desarrollo", Toast.LENGTH_SHORT).show()
    }

    // ==================== VALIDACIÓN Y FORMULARIO ====================

    /**
     * Valida todos los campos del formulario.
     * @return el producto ingresado, o null si hay errores (se muestran en cada campo).
     *
     * NOTA PARA EL EQUIPO (Parte 5): reutilizar esta función en Modificar Producto.
     */
    private fun leerFormulario(): Producto? {
        limpiarErrores()

        val codigo = codigoIngresado()
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precioTexto = etPrecio.text.toString().trim()
        val precio = precioTexto.toDoubleOrNull()
        val marca = etMarca.text.toString().trim()

        // Se revisan todos los campos para mostrar todos los errores a la vez
        val errores = mutableListOf<Pair<TextInputLayout, String>>()
        when {
            codigo.isEmpty() -> errores += tilCodigo to "Ingresa el código del producto"
            !codigo.matches(PATRON_CODIGO) -> errores += tilCodigo to "Usa solo letras, números o guiones (-)"
        }
        if (nombre.isEmpty()) errores += tilNombre to "Ingresa el nombre del producto"
        if (descripcion.isEmpty()) errores += tilDescripcion to "Ingresa la descripción del producto"
        when {
            precioTexto.isEmpty() -> errores += tilPrecio to "Ingresa el precio del producto"
            precio == null || precio <= 0 -> errores += tilPrecio to "El precio debe ser un número mayor a 0"
        }
        if (marca.isEmpty()) errores += tilMarca to "Ingresa la marca del producto"

        if (errores.isNotEmpty() || precio == null) {
            errores.forEach { (til, mensaje) -> til.error = mensaje }
            errores.firstOrNull()?.first?.editText?.requestFocus()
            Toast.makeText(this, "Por favor, corrige los campos marcados", Toast.LENGTH_SHORT).show()
            return null
        }

        return Producto(codigo, nombre, descripcion, precio, marca)
    }

    // El código se guarda siempre en mayúsculas para que "p001" y "P001" sean el mismo producto
    private fun codigoIngresado(): String = etCodigo.text.toString().trim().uppercase()

    private fun mostrarEnFormulario(producto: Producto) {
        etCodigo.setText(producto.codigo)
        etNombre.setText(producto.nombre)
        etDescripcion.setText(producto.descripcion)
        etPrecio.setText("%.2f".format(Locale.US, producto.precio))
        etMarca.setText(producto.marca)
        limpiarErrores()
    }

    private fun limpiarFormulario() {
        etCodigo.text.clear()
        limpiarDetalle()
        limpiarErrores()
    }

    // Limpia todos los campos excepto el código
    private fun limpiarDetalle() {
        etNombre.text.clear()
        etDescripcion.text.clear()
        etPrecio.text.clear()
        etMarca.text.clear()
    }

    private fun camposFormulario() = listOf(tilCodigo, tilNombre, tilDescripcion, tilPrecio, tilMarca)

    private fun limpiarErrores() = camposFormulario().forEach { quitarError(it) }

    // Desactivar el error también libera el espacio que ocupaba el mensaje
    private fun quitarError(til: TextInputLayout) {
        til.error = null
        til.isErrorEnabled = false
    }

    // ==================== UTILIDADES DE PANTALLA ====================

    private fun ocultarTeclado() {
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }

    // Evita que el contenido quede debajo de la barra de estado, la de navegación o el teclado
    private fun ajustarBordesPantalla() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val bordes = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
            )
            vista.setPadding(bordes.left, bordes.top, bordes.right, bordes.bottom)
            insets
        }
    }
}
