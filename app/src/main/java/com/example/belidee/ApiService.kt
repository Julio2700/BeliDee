package com.example.belidee

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("products/category/mens-shoes")
    fun getShoes(): Call<ShoeResponse>
}