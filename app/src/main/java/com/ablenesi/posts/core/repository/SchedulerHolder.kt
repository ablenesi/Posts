package com.ablenesi.posts.core.repository

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerHolder(
    val network: Scheduler = Schedulers.io(),
    val ui: Scheduler = AndroidSchedulers.mainThread()
)