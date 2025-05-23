package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.CategoryDataModels
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val repo: Repo) {

    fun deleteCategory(categoryName: String) : Flow<ResultState<CategoryDataModels>>{
        return repo.deleteCategory(categoryName)
    }
}