package com.example.belidee

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShoeProduct::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoeDao(): ShoeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "belidee_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}