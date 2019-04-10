package com.ablenesi.posts.core.repository

import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.ablenesi.posts.core.networking.PostsRemoteSource
import com.ablenesi.posts.core.networking.dto.CommentDTO
import com.ablenesi.posts.core.networking.dto.PostDTO
import com.ablenesi.posts.core.networking.dto.UserDTO
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PostsRepositoryTest {

    private lateinit var remoteSource: PostsRemoteSource
    private lateinit var sut: PostsRepository

    @Before
    fun setUp() {
        remoteSource = mock()
        sut = PostsRepository(remoteSource, SchedulerHolder(Schedulers.trampoline(), Schedulers.trampoline()))
    }

    @Test
    fun `WHEN getPosts is called THEN correct Post list is returned`() {
        whenever(remoteSource.posts()).thenReturn(Single.just(postDTOs))

        val testObserver = sut.getPosts().test()

        testObserver.assertValue(posts)
    }

    @Test
    fun `WHEN getPosts is called first time THEN remote source is called`() {
        val testObserver = TestObserver<Any>()
        whenever(remoteSource.posts()).thenReturn(
            Single.just(listOf<PostDTO>()).doOnSubscribe(testObserver::onSubscribe)
        )

        sut.getPosts().test()

        verify(remoteSource).posts()
        testObserver.assertSubscribed()
    }

    @Test
    fun `WHEN getPosts is called second time THEN remote source is not called`() {
        var count = 0
        whenever(remoteSource.posts()).thenReturn(
            Single.just(listOf<PostDTO>()).doOnSubscribe { count++ }
        )

        sut.getPosts().test()
        sut.getPosts().test()

        assertEquals(1, count) // verifying that the remote source has only one subscription
    }

    @Test
    fun `WHEN getPostDetail is called THEN the returned PostDetail is the combined information from remote source`() {
        whenever(remoteSource.posts()).thenReturn(Single.just(postDTOs))
        whenever(remoteSource.users()).thenReturn(Single.just(listOf(userDTO)))
        whenever(remoteSource.comments()).thenReturn(Single.just(commentDTOs))

        val testObserver = sut.getPostDetail(POST_ID).test()

        testObserver.assertValue(PostDetail(post, BODY, USER_NAME, numberOfCommentsToFirstPost))
    }

    companion object {
        private const val POST_ID = 1
        private const val ANOTHER_POST_ID = 2
        private const val USER_ID = 2
        private const val TITLE = "title"
        private const val BODY = "body"
        private const val USER_NAME = "title"

        private val postDTO = PostDTO(POST_ID, USER_ID, TITLE, BODY)
        private val postDTOs = listOf(postDTO)

        private val post = Post(POST_ID, TITLE)
        private val posts = listOf(post)

        private val userDTO = UserDTO(USER_ID, USER_NAME)
        private const val numberOfCommentsToFirstPost = 2
        private val commentDTOs = listOf(CommentDTO(POST_ID), CommentDTO(POST_ID), CommentDTO(ANOTHER_POST_ID))
    }
}