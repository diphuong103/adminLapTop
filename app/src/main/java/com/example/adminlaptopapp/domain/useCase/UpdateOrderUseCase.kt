package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.OrderDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateOrderUseCase @Inject constructor(private val repo: Repo) {

    fun updateOrder(order: OrderDataModels, postalCode: String): Flow<ResultState<String>>{
        return repo.updateOrder(order, postalCode)
    }
}