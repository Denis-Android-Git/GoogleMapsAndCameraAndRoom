package com.example.myapplication.data

class InfoRepository(
    private val retrofitAndApi: RetrofitAndApi.PlacesApi
) {
    suspend fun getInfoDto(
        xid: String
    ): DetailInfoDto {
        return retrofitAndApi.getInfo(xid)
    }
}