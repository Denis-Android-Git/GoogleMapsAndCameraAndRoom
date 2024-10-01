package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.entity.db.Photo
import com.example.myapplication.entity.db.Place
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Upsert
    suspend fun upsertPlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)

    @Query("select * from Place where id = :id")
    suspend fun getPlaceById(id: String): Place

    @Query("select * from Place")
    fun getPlaces(): Flow<List<Place>>

    @Upsert
    suspend fun upsertPhoto(photo: Photo)

    @Delete
    suspend fun deleteAllPhotos(photoList: List<Photo>)

    @Delete
    suspend fun deleteOnePhoto(photo: Photo)

    @Query("select * from Photo")
    fun getAll(): Flow<List<Photo>>

    @Query("select * from Photo where uri = :id")
    suspend fun getPhotoById(id: String): Photo
}