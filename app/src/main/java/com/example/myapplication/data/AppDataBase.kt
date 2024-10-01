package com.example.myapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.entity.db.Photo
import com.example.myapplication.entity.db.Place

@Database(
    entities = [
        Photo::class,
        Place::class
    ], version = 9
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}