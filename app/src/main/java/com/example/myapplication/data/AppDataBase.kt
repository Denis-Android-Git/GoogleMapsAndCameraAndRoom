package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.entity.Photo

@Database(
    entities = [
        Photo::class
    ], version = 7
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}