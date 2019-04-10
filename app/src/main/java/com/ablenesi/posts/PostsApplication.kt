package com.ablenesi.posts

import android.app.Application
import com.ablenesi.posts.di.appModule
import com.ablenesi.posts.di.coreModule
import org.koin.core.context.startKoin

class PostsApplication : Application() {

    override fun onCreate() {
        startKoin { modules(coreModule, appModule) }
        super.onCreate()
    }
}