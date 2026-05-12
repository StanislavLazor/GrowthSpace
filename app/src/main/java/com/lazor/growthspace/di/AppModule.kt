package com.lazor.growthspace.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lazor.growthspace.data.repository.AuthRepository
import com.lazor.growthspace.data.repository.AuthRepositoryImpl
import com.lazor.growthspace.ui.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // 1. Firebase інстанси (один на весь додаток)
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // 2. Репозиторії
    // get() автоматично знайде і підставить FirebaseAuth та FirebaseFirestore
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // 3. ViewModels
    viewModel { AuthViewModel(get()) }
}