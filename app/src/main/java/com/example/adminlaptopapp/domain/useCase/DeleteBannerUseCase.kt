package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteBannerUseCase @Inject constructor(private val repo: Repo) {

    fun deleteBanner(bannerName: String) : Flow<ResultState<BannerDataModels>>{
        return repo.deleteBanner(bannerName)
    }
}