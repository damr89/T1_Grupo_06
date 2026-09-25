package com.example.t1_grupo_06.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.t1_grupo_06.R

/**
 * Adaptador de la lista de productos registrados.
 * Al tocar un producto se ejecuta [alSeleccionar] para mostrarlo en el formulario.
 */
class ProductoAdapter(
    private val alSeleccionar: (Producto) -> Unit
) : ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ComparadorProductos) {

    class ProductoViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvCodigo: TextView = vista.findViewById(R.id.tvCodigoItem)
        val tvNombre: TextView = vista.findViewById(R.id.tvNombreItem)
        val tvDetalle: TextView = vista.findViewById(R.id.tvDetalleItem)
        val tvPrecio: TextView = vista.findViewById(R.id.tvPrecioItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = getItem(position)
        holder.tvCodigo.text = producto.codigo
        holder.tvNombre.text = producto.nombre
        holder.tvDetalle.text = "${producto.marca} · ${producto.descripcion}"
        holder.tvPrecio.text = producto.precioFormateado()
        holder.itemView.setOnClickListener { alSeleccionar(producto) }
    }
}

// Permite a la lista animar solo los elementos que cambiaron
private object ComparadorProductos : DiffUtil.ItemCallback<Producto>() {
    override fun areItemsTheSame(anterior: Producto, nuevo: Producto) =
        anterior.codigo == nuevo.codigo

    override fun areContentsTheSame(anterior: Producto, nuevo: Producto) =
        anterior == nuevo
}
