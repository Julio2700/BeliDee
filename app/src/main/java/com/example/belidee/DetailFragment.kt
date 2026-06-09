package com.example.belidee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val ivDetailShoe = view.findViewById<ImageView>(R.id.ivDetailShoe)
        val tvDetailTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val tvDetailPrice = view.findViewById<TextView>(R.id.tvDetailPrice)

        val title = arguments?.getString("title")
        val price = arguments?.getDouble("price") ?: 0.0
        val thumbnail = arguments?.getString("thumbnail")

        tvDetailTitle.text = title
        tvDetailPrice.text = "$$price"

        Glide.with(this)
            .load(thumbnail)
            .into(ivDetailShoe)

        return view
    }
}