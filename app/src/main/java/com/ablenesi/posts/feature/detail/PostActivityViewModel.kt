package com.ablenesi.posts.feature.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.ablenesi.posts.core.repository.PostsRepository
import io.reactivex.disposables.Disposable

class PostActivityViewModel(private val repository: PostsRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _postDetail = MutableLiveData<PostDetail>()
    val postDetail: LiveData<PostDetail> = _postDetail

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private var disposable: Disposable? = null

    init {
        _loading.value = true
    }

    fun setPost(post: Post) {
        _title.value = post.title
        disposable = repository.getPostDetail(post.id)
            .subscribe({
                it?.let {
                    _postDetail.value = it
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
