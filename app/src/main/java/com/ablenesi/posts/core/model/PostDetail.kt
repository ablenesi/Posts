package com.ablenesi.posts.core.model

data class PostDetail(
    private val post: Post,
    val content: String,
    val userName: String,
    val comments: Int
) {
    val id = post.id
    val title = post.title
}