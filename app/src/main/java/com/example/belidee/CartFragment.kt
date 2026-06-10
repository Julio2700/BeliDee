package com.example.belidee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.util.concurrent.Executors

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val layoutEmpty = view.findViewById<LinearLayout>(R.id.layoutEmptyCart)
        val layoutFilled = view.findViewById<LinearLayout>(R.id.layoutFilledCart)
        val btnCheckout = view.findViewById<MaterialButton>(R.id.btnCheckout)
        val rvCartItems = view.findViewById<RecyclerView>(R.id.rvCartItems)

        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            val db = AppDatabase.getDatabase(requireContext())
            // PERBAIKAN UTAMA: Hanya membaca kiriman barang dari tabel keranjang asli!
            val cartItems = db.shoeDao().getCartItems()

            requireActivity().runOnUiThread {
                if (cartItems.isEmpty()) {
                    layoutEmpty.visibility = View.VISIBLE
                    layoutFilled.visibility = View.GONE
                    btnCheckout.visibility = View.GONE
                } else {
                    layoutEmpty.visibility = View.GONE
                    layoutFilled.visibility = View.VISIBLE
                    btnCheckout.visibility = View.VISIBLE

                    rvCartItems.layoutManager = LinearLayoutManager(requireContext())
                    rvCartItems.adapter = CartAdapter(cartItems)

                    var subtotal = 0.0
                    for (item in cartItems) {
                        subtotal += item.price
                    }

                    val shipping = 5.0
                    val total = subtotal + shipping

                    view.findViewById<TextView>(R.id.tvSubtotal).text = "$ $subtotal"
                    view.findViewById<TextView>(R.id.tvEstimatedTotal).text = "$ $total + Tax"
                }
            }
        }

        view.findViewById<ImageView>(R.id.navSearch).setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }
}