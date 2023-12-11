package com.example.myapplication.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.opentripmap.com"

class RetrofitAndApi {
    val api: PlacesApi by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
                    it.level = HttpLoggingInterceptor.Level.BODY
                }).build()
            )
            .build()
            .create(PlacesApi::class.java)
    }

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
    }
}
