package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.ProductDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateProductUseCase @Inject constructor(private val repo: Repo) {

    fun updateproduct(product: ProductDataModels , productId: String) : Flow<ResultState<String>>{
        return repo.updateProduct(product , productId)
    }
}