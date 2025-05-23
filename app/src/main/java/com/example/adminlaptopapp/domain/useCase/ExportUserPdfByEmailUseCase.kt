package com.example.adminlaptopapp.domain.useCase

import com.example.adminlaptopapp.common.ResultState
import com.example.adminlaptopapp.domain.models.UserDataParent
import com.example.adminlaptopapp.domain.repo.Repo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExportUserPdfByEmailUseCase @Inject constructor(private val repo: Repo) {

    fun exportUserPdfByEmail(email: String) : Flow<ResultState<UserDataParent>>{
        return repo.getUserByEmail(email)
    }
}