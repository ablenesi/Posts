package com.ablenesi.posts.core.repository

import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.ablenesi.posts.core.networking.PostsRemoteSource
import com.ablenesi.posts.core.networking.dto.CommentDTO
import com.ablenesi.posts.core.networking.dto.PostDTO
import com.ablenesi.posts.core.networking.dto.UserDTO
import com.ablenesi.posts.core.networking.dto.toPost
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3

class PostsRepository(
    private val remoteSource: PostsRemoteSource,
    private val schedulers: SchedulerHolder
) {

    fun getPosts(): Single<List<Post>> =
        getPostDTOs().map { posts: List<PostDTO> -> posts.map(PostDTO::toPost) }
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.ui)

    fun getPostDetail(id: Int): Single<PostDetail> =
        Single.zip(
            getPostDTOs().map { posts -> posts.first { it.id == id } }
                .subscribeOn(schedulers.network)
                .observeOn(schedulers.ui),
            getUserDTOs()
                .subscribeOn(schedulers.network)
                .observeOn(schedulers.ui),
            getCommentDTOs()
                .subscribeOn(schedulers.network)
                .observeOn(schedulers.ui),
            Function3 { post, users, comments ->
                val user = users.first { it.id == post.userId }
                PostDetail(post.toPost(), post.body, user.username, comments.count { it.postId == post.id })
            })

    fun getPostsWithDetails(): Single<List<PostDetail>> =
        getPosts().flatMap {
            Observable.fromIterable(it)
                .flatMap { post -> getPostDetail(post.id).toObservable() }
                .toList()
        }

    private var posts: List<PostDTO>? = null
    private var users: List<UserDTO>? = null
    private var comments: List<CommentDTO>? = null

    private fun getPostDTOs(): Single<List<PostDTO>> =
        Single.fromCallable { posts!! }
            .onErrorResumeNext(remoteSource.posts().doOnSuccess { posts = it })

    private fun getUserDTOs(): Single<List<UserDTO>> =
        Single.fromCallable { users!! }
            .onErrorResumeNext(remoteSource.users().doOnSuccess { users = it })

    private fun getCommentDTOs(): Single<List<CommentDTO>> =
        Single.fromCallable { comments!! }
            .onErrorResumeNext(remoteSource.comments().doOnSuccess { comments = it })
}

