package com.example.belidee

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
        rvShoes.layoutManager = LinearLayoutManager(requireContext())

        btnRefresh.setOnClickListener {
            fetchShoeData()
        }

        fetchShoeData()

        return view
    }

    private fun fetchShoeData() {
        progressBar.visibility = View.VISIBLE
        rvShoes.visibility = View.GONE
        btnRefresh.visibility = View.GONE

        RetrofitClient.instance.getShoes().enqueue(object : retrofit2.Callback<ShoeResponse> {
            override fun onResponse(call: retrofit2.Call<ShoeResponse>, response: retrofit2.Response<ShoeResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val shoeList = response.body()!!.results
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
                    // TAMPILKAN ERROR HTTP (Misal: 404 Not Found, 403 Forbidden)
                    requireActivity().runOnUiThread {
                        android.widget.Toast.makeText(requireContext(), "Error API: ${response.code()}", android.widget.Toast.LENGTH_LONG).show()
                    }
                    android.util.Log.e("BeliDee_Error", "HTTP Error: ${response.code()} - ${response.errorBody()?.string()}")
                    loadLocalData()
                }
            }

            override fun onFailure(call: retrofit2.Call<ShoeResponse>, t: Throwable) {
                // TAMPILKAN ERROR SISTEM/GSON (Misal: Tidak ada internet, Format JSON salah)
                requireActivity().runOnUiThread {
                    android.widget.Toast.makeText(requireContext(), "Gagal: ${t.message}", android.widget.Toast.LENGTH_LONG).show()
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