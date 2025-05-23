package com.example.adminlaptopapp.domain.di

import android.content.Context
import com.example.adminlaptopapp.data.repo.RepoImpl
import com.example.adminlaptopapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    fun provideRepo(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore, @ApplicationContext context: Context): Repo {
        return RepoImpl(firebaseAuth , firebaseFirestore , context)
    }

}