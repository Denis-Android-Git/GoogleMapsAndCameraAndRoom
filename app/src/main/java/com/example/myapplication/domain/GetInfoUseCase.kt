package com.example.myapplication.domain

import com.example.myapplication.data.dto.DetailInfoDto
import com.example.myapplication.data.repository.InfoRepository

class GetInfoUseCase(
    private val infoRepository: InfoRepository
) {

    suspend fun execute(
        xid: String
    ): DetailInfoDto {
        return infoRepository.getInfoDto(xid)
    }
}