package com.ablenesi.posts.core.networking.dto

import com.ablenesi.posts.core.model.Post

data class PostDTO(val id: Int, val userId: Int, val title: String, val body: String)

fun PostDTO.toPost(): Post = Post(id, title)