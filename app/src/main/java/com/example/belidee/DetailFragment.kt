package com.example.belidee

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.util.concurrent.Executors

class DetailFragment : Fragment() {

    private var selectedSize: String? = null
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val title = arguments?.getString("title") ?: ""
        val price = arguments?.getDouble("price") ?: 0.0
        val thumbnail = arguments?.getString("thumbnail") ?: ""

        view.findViewById<TextView>(R.id.tvDetailTitle).text = title
        view.findViewById<TextView>(R.id.tvDetailPrice).text = "$ $price"
        Glide.with(this).load(thumbnail).into(view.findViewById(R.id.ivDetailShoe))

        val rvSizes = view.findViewById<RecyclerView>(R.id.rvSizes)
        val sizes = (35..45).map { it.toString() }

        rvSizes.layoutManager = GridLayoutManager(requireContext(), 4)
        rvSizes.adapter = SizeAdapter(sizes) { size ->
            selectedSize = size
        }

        view.findViewById<MaterialButton>(R.id.btnAddToCart).setOnClickListener {
            if (selectedSize == null) {
                Toast.makeText(requireContext(), "Pilih ukuran dulu, Bos!", Toast.LENGTH_SHORT).show()
            } else {
                executor.execute {
                    val db = AppDatabase.getDatabase(requireContext())

                    // PERBAIKAN: Masuk ke tabel CartItem asli beserta ukuran kakinya
                    val itemKeranjang = CartItem(
                        title = title,
                        price = price,
                        thumbnail = thumbnail,
                        size = selectedSize!!
                    )
                    db.shoeDao().insertCartItem(itemKeranjang)

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Berhasil masuk keranjang!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.cartFragment)
                    }
                }
            }
        }
        return view
    }
}

class SizeAdapter(private val list: List<String>, private val onSizeSelected: (String) -> Unit) :
    RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    private var selectedPosition = -1

    class SizeViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val btn = v as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val tv = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(180, 120)
            gravity = android.view.Gravity.CENTER
            setPadding(10, 10, 10, 10)
            background = android.graphics.drawable.ColorDrawable(Color.parseColor("#F5F5F5"))
            setTextColor(Color.BLACK)
        }
        return SizeViewHolder(tv)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.btn.text = list[position]

        if (selectedPosition == position) {
            holder.btn.setBackgroundColor(Color.BLACK)
            holder.btn.setTextColor(Color.WHITE)
        } else {
            holder.btn.setBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.btn.setTextColor(Color.BLACK)
        }

        holder.btn.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onSizeSelected(list[position])
        }
    }

    override fun getItemCount() = list.size
}