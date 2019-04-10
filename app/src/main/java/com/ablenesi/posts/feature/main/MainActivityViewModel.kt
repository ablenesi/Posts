package com.ablenesi.posts.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.repository.PostsRepository
import io.reactivex.disposables.Disposable

class MainActivityViewModel(postsRepository: PostsRepository) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    private val _loading = MutableLiveData<Boolean>()

    val posts: LiveData<List<Post>> = _posts
    val loading: LiveData<Boolean> = _loading
    var disposable: Disposable? = null

    init {
        _loading.value = true
        disposable = postsRepository.getPosts().subscribe({
            it?.let {
                _posts.value = it
                _loading.value = false
            }
        }, {
            // TODO Error Handling
        })
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}
