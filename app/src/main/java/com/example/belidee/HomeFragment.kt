package com.example.belidee

import android.os.Bundle
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

class HomeFragment : Fragment() {

    private lateinit var rvShoes: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRefresh: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rvShoes = view.findViewById(R.id.rvShoes)
        progressBar = view.findViewById(R.id.progressBar)
        btnRefresh = view.findViewById(R.id.btnRefresh)

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

        RetrofitClient.instance.getShoes().enqueue(object : Callback<ShoeResponse> {
            override fun onResponse(call: Call<ShoeResponse>, response: Response<ShoeResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    rvShoes.visibility = View.VISIBLE
                    val shoeList = response.body()!!.products
                    rvShoes.adapter = ShoeAdapter(shoeList) { shoe ->
                        findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<ShoeResponse>, t: Throwable) {
                showError()
            }
        })
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        rvShoes.visibility = View.GONE
        btnRefresh.visibility = View.VISIBLE
    }
}