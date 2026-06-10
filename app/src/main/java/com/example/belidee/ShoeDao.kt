package com.example.belidee

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShoeDao {
    // --- KHUSUS KATALOG HOME ---
    @Query("SELECT * FROM shoes")
    fun getAllShoes(): List<ShoeProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shoes: List<ShoeProduct>)

    @Query("DELETE FROM shoes")
    fun deleteAll()

    // --- KHUSUS KERANJANG BELANJA (CART) ---
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): List<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartItem(item: CartItem)
}