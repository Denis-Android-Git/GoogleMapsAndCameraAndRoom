package com.example.myapplication.domain

import com.example.myapplication.data.DetailInfoDto
import com.example.myapplication.data.InfoRepository

class GetInfoUseCase(
    xid: String
) {
    private val infoRepository = InfoRepository(xid)

    suspend fun execute(): DetailInfoDto {
        return infoRepository.getInfoDto()
    }
}