package com.example.belidee

data class ShoeResponse(
    val products: List<ShoeProduct>
)

data class ShoeProduct(
    val id: Int,
    val title: String,
    val price: Double,
    val thumbnail: String
)