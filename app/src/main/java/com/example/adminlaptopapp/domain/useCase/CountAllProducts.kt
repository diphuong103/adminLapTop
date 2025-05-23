package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CountAllProducts @Inject constructor(private val repo: Repo){

    fun countAllProducts(): Flow<ResultState<String>>{
        return repo.countAllProducts()
    }
}