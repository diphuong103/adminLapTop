package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.UserDataParent
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllUserUseCase @Inject constructor(private val repo: Repo) {

    fun getAllUser() : Flow<ResultState<List<UserDataParent>>>{
        return repo.getAllUsers()
    }
}