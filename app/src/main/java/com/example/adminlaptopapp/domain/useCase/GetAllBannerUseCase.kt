package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBannerUseCase @Inject constructor(private val repo: Repo) {

    fun getAllBanner(): Flow<ResultState<List<BannerDataModels>>> {
        return repo.getAllBanners()
    }
}