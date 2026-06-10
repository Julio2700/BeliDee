package com.example.belidee

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors

class HomeFragment : Fragment() {

    private lateinit var rvShoes: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRefresh: Button
    private lateinit var database: AppDatabase
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rvShoes = view.findViewById(R.id.rvShoes)
        progressBar = view.findViewById(R.id.progressBar)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        database = AppDatabase.getDatabase(requireContext())
        rvShoes.layoutManager = GridLayoutManager(requireContext(), 2)

        btnRefresh.setOnClickListener {
            fetchShoeData()
        }

        // =========================================================
        // FITUR 1: TOMBOL GANTI TEMA (DARK/LIGHT MODE)
        // =========================================================
        val btnThemeToggle = view.findViewById<ImageView>(R.id.btnThemeToggle)
        btnThemeToggle.setOnClickListener {
            // Cek status tema sistem saat ini
            val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

            if (isNightMode) {
                // Paksa pindah ke Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                // Paksa pindah ke Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        // =========================================================
        // FITUR 2: NAVIGASI BAWAH (BOTTOM NAV)
        // =========================================================
        val navCart = view.findViewById<ImageView>(R.id.navCart)
        val navWishlist = view.findViewById<ImageView>(R.id.navWishlist)
        val navProfile = view.findViewById<ImageView>(R.id.navProfile)

        // Pindah ke halaman Keranjang
        navCart.setOnClickListener {
            try {
                // CATATAN: Pastikan Anda sudah menambahkan CartFragment ke dalam file nav_graph.xml
                findNavController().navigate(R.id.cartFragment)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Tambahkan id cartFragment di nav_graph.xml dulu ya!", Toast.LENGTH_LONG).show()
            }
        }

        navWishlist.setOnClickListener {
            Toast.makeText(requireContext(), "Halaman Barang yang di-Like segera hadir", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Halaman Akun segera hadir", Toast.LENGTH_SHORT).show()
        }

        fetchShoeData()

        return view
    }

    private fun fetchShoeData() {
        progressBar.visibility = View.VISIBLE
        rvShoes.visibility = View.GONE
        btnRefresh.visibility = View.GONE

        RetrofitClient.instance.getShoes().enqueue(object : Callback<List<ShoeProduct>> {

            override fun onResponse(
                call: Call<List<ShoeProduct>>,
                response: Response<List<ShoeProduct>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val shoeList = response.body()!!
                    executor.execute {
                        database.shoeDao().deleteAll()
                        database.shoeDao().insertAll(shoeList)
                        handler.post {
                            progressBar.visibility = View.GONE
                            rvShoes.visibility = View.VISIBLE
                            displayData(shoeList)
                        }
                    }
                } else {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Error API: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                    android.util.Log.e("BeliDee_Error", "HTTP Error: ${response.code()} - ${response.errorBody()?.string()}")
                    loadLocalData()
                }
            }

            override fun onFailure(call: Call<List<ShoeProduct>>, t: Throwable) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Gagal: ${t.message}", Toast.LENGTH_LONG).show()
                }
                android.util.Log.e("BeliDee_Error", "Failure: ${t.message}")
                loadLocalData()
            }

        })
    }

    private fun loadLocalData() {
        executor.execute {
            val localShoes = database.shoeDao().getAllShoes()
            handler.post {
                progressBar.visibility = View.GONE
                if (localShoes.isNotEmpty()) {
                    rvShoes.visibility = View.VISIBLE
                    displayData(localShoes)
                } else {
                    rvShoes.visibility = View.GONE
                    btnRefresh.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun displayData(shoeList: List<ShoeProduct>) {
        rvShoes.adapter = ShoeAdapter(shoeList) { shoe ->
            val bundle = Bundle().apply {
                putString("title", shoe.title)
                putDouble("price", shoe.price)
                putString("thumbnail", shoe.thumbnail)
            }
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment, bundle)
        }
    }
}