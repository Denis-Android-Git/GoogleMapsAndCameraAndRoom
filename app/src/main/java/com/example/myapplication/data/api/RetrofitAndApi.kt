package com.example.myapplication.data.api

import com.example.myapplication.data.dto.DetailInfoDto
import com.example.myapplication.data.dto.PlacesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val BASE_URL = "https://api.opentripmap.com"

class RetrofitAndApi {

    interface PlacesApi {
        @GET("/0.1/ru/places/radius?radius=10000&apikey=5ae2e3f221c38a28845f05b6264a26c0455ff3893a3c0bb528719ce4")
        suspend fun getPlaces(
            @Query("lon") lon: Double,
            @Query("lat") lat: Double
        ): PlacesDto

        @GET("/0.1/ru/places/xid/{xid}?apikey=5ae2e3f221c38a28845f05b6264a26c0455ff3893a3c0bb528719ce4")
        suspend fun getInfo(
            @Path("xid") xid: String
        ): DetailInfoDto

        @GET("/0.1/ru/places/bbox?apikey=5ae2e3f221c38a28845f05b6264a26c0455ff3893a3c0bb528719ce4")
        suspend fun search(
            @Query("name") name: String,
            @Query("lon_min") lon_min: Double?,
            @Query("lat_min") lat_min: Double?,
            @Query("lon_max") lon_max: Double?,
            @Query("lat_max") lat_max: Double?
        ): PlacesDto
    }
}
