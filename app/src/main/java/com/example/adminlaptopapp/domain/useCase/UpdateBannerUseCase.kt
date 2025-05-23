package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.BannerDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateBannerUseCase @Inject constructor(private val repo: Repo) {

    fun updateBanner(banner: BannerDataModels, bannerId: String): Flow<ResultState<String>>{
        return repo.updateBanner(banner, bannerId)
    }
}