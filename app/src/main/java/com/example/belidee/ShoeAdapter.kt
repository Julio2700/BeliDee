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
    private val onItemClick: (ShoeProduct) -> Unit
) : RecyclerView.Adapter<ShoeAdapter.ShoeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shoe, parent, false)
        return ShoeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoeViewHolder, position: Int) {
        val shoe = shoes[position]
        holder.bind(shoe, onItemClick)
    }

    override fun getItemCount(): Int = shoes.size

    class ShoeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivShoe: ImageView = itemView.findViewById(R.id.ivShoe)
        private val tvShoeTitle: TextView = itemView.findViewById(R.id.tvShoeTitle)
        private val tvShoePrice: TextView = itemView.findViewById(R.id.tvShoePrice)

        fun bind(shoe: ShoeProduct, onItemClick: (ShoeProduct) -> Unit) {
            tvShoeTitle.text = shoe.title
            tvShoePrice.text = "$${shoe.price}"

            Glide.with(itemView.context)
                .load(shoe.thumbnail)
                .into(ivShoe)

            itemView.setOnClickListener { onItemClick(shoe) }
        }
    }
}