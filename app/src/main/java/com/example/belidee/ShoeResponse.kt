package com.example.belidee

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ShoeResponse(
    @SerializedName("products")
    val results: List<ShoeProduct>
)

@Entity(tableName = "shoes")
data class ShoeProduct(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,

    @SerializedName("title")
    val title: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("thumbnail")
    val thumbnail: String
)