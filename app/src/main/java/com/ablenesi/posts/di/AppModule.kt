package com.ablenesi.posts.di

import com.ablenesi.posts.core.networking.PostsRemoteSource
import com.ablenesi.posts.core.repository.PostsRepository
import com.ablenesi.posts.core.repository.SchedulerHolder
import com.ablenesi.posts.feature.detail.PostActivityViewModel
import com.ablenesi.posts.feature.main.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    viewModel { MainActivityViewModel(get()) }
    viewModel { PostActivityViewModel(get()) }
}

val coreModule = module {
    single { createWebService<PostsRemoteSource>("https://jsonplaceholder.typicode.com") }
    single { PostsRepository(get(), get()) }
    single { SchedulerHolder() }
}

inline fun <reified T> createWebService(url: String): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    return retrofit.create(T::class.java)
}