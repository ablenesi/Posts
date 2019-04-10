package com.ablenesi.posts.core.networking

import com.ablenesi.posts.core.networking.dto.CommentDTO
import com.ablenesi.posts.core.networking.dto.PostDTO
import com.ablenesi.posts.core.networking.dto.UserDTO
import io.reactivex.Single
import retrofit2.http.GET

interface PostsRemoteSource {
    @GET("posts")
    fun posts(): Single<List<PostDTO>>

    @GET("users")
    fun users(): Single<List<UserDTO>>

    @GET("comments")
    fun comments(): Single<List<CommentDTO>>
}