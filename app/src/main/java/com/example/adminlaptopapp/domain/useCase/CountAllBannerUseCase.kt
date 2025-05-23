package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CountAllBannerUseCase @Inject constructor(private val repo: Repo) {
    fun countAllBanner(): Flow<ResultState<String>> {
        return repo.countAllBanners()
    }
}