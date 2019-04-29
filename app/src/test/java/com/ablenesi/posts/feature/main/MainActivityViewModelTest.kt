package com.ablenesi.posts.feature.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.ablenesi.posts.core.repository.PostsRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sut: MainActivityViewModel

    private lateinit var mockPostsRepository: PostsRepository
    @Before
    fun setUp() {
        mockPostsRepository = mock()
        whenever(mockPostsRepository.getPostsWithDetails()).thenReturn(Single.error(Throwable()))
    }

    @Test
    fun `default state is loading`() {
        sut = MainActivityViewModel(mockPostsRepository)

        assert(sut.loading.value == true)
    }

    @Test
    fun `WHEN viewModel is initialise THAN repo getPosts is called`() {
        val testObserver = TestObserver<Unit>()
        val getPosts = Single.just(POSTS)
            .doOnSubscribe(testObserver::onSubscribe)
        whenever(mockPostsRepository.getPostsWithDetails()).thenReturn(getPosts)

        sut = MainActivityViewModel(mockPostsRepository)

        testObserver.assertSubscribed()
    }

    @Test
    fun `WHEN getPosts succeeds THAN posts is updated`() {
        val getPosts = Single.just(POSTS)
        whenever(mockPostsRepository.getPostsWithDetails()).thenReturn(getPosts)

        sut = MainActivityViewModel(mockPostsRepository)

        assertEquals(POSTS, sut.posts.value)
    }

    @Test
    fun `WHEN getPosts succeeds THAN loading is false`() {
        val getPosts = Single.just(POSTS)
        whenever(mockPostsRepository.getPostsWithDetails()).thenReturn(getPosts)

        sut = MainActivityViewModel(mockPostsRepository)

        assert(sut.loading.value == false)
    }

    companion object {
        private val POSTS = listOf(PostDetail(Post(1, "test1"),"","",0))
    }
}