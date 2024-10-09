package com.example.myapplication.data.repository

import com.example.myapplication.data.api.RetrofitAndApi
import com.example.myapplication.data.dto.DetailInfoDto

class InfoRepository(
    private val retrofitAndApi: RetrofitAndApi.PlacesApi
) {
    suspend fun getInfoDto(
        xid: String
    ): DetailInfoDto {
        return retrofitAndApi.getInfo(xid)
    }
}