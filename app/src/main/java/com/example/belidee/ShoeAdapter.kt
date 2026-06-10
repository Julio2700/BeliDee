package com.example.belidee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ShoeAdapter(
    private val shoes: List<ShoeProduct>,
    private val onClick: (ShoeProduct) -> Unit
) : RecyclerView.Adapter<ShoeAdapter.ShoeViewHolder>() {

    class ShoeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivShoe: ImageView = view.findViewById(R.id.ivShoe)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shoe, parent, false)
        return ShoeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoeViewHolder, position: Int) {
        val shoe = shoes[position]

        // Nama produk
        holder.tvTitle.text = shoe.title

        // Format harga biar terlihat elegan seperti Rp. 1.600.000 (disini kita pakai Dollar $ karena data DummyJSON)
        holder.tvPrice.text = "$ ${shoe.price}"

        // Load gambar sepatu tanpa memotongnya
        Glide.with(holder.itemView.context)
            .load(shoe.thumbnail)
            .into(holder.ivShoe)

        // Klik untuk ke halaman Detail
        holder.itemView.setOnClickListener {
            onClick(shoe)
        }
    }

    override fun getItemCount() = shoes.size
}