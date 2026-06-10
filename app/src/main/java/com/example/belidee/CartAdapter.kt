package com.example.belidee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvCartItemTitle)
        val price: TextView = view.findViewById(R.id.tvCartItemPrice)
        val image: ImageView = view.findViewById(R.id.ivCartItemShoe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.title.text = item.title
        holder.price.text = "$ ${item.price}"
        Glide.with(holder.itemView.context).load(item.thumbnail).into(holder.image)
    }

    override fun getItemCount() = cartItems.size
}