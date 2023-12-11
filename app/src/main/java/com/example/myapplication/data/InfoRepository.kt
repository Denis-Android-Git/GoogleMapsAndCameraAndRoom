package com.example.myapplication.data

class InfoRepository(
    private val xid: String
) {
    private val retrofitAndApi = RetrofitAndApi()
    suspend fun getInfoDto(): DetailInfoDto  {
        return retrofitAndApi.api.getInfo(xid)
    }
}