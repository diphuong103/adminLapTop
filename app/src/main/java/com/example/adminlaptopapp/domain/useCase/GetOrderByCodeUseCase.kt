package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrderByCodeUseCase @Inject constructor(private val repo: Repo) {

    fun getOrderByCode(orderCode: String) : Flow<ResultState<OrderDataModels>>{
        return repo.getOrderByCode(orderCode)
    }
}