package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllOrderUseCase @Inject constructor(private val repo: Repo) {

    fun getAllOrder() : Flow<ResultState<List<OrderDataModels>>>{
        return repo.getAllOrders()
    }
}