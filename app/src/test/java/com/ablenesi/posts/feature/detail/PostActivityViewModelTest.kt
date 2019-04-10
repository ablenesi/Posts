package com.ablenesi.posts.feature.detail

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

class PostActivityViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var sut: PostActivityViewModel

    private lateinit var mockPostsRepository: PostsRepository

    @Before
    fun setUp() {
        mockPostsRepository = mock()
        whenever(mockPostsRepository.getPosts()).thenReturn(Single.error(Throwable()))
    }

    @Test
    fun `default state is loading`() {
        sut = PostActivityViewModel(mockPostsRepository)

        assert(sut.loading.value == true)
    }

    @Test
    fun `WHEN viewModel setPost is called THAN title is updated`() {
        sut = PostActivityViewModel(mockPostsRepository)
        whenever(mockPostsRepository.getPostDetail(POST_ID)).thenReturn(Single.just(POST_DETAIL))

        sut.setPost(POST)

        assertEquals(POST.title, sut.title.value)
    }

    @Test
    fun `WHEN viewModel setPost is called THAN repo getPostDetail is called`() {
        val testObserver = TestObserver<Unit>()
        val getPostDetail = Single.just(POST_DETAIL)
            .doOnSubscribe(testObserver::onSubscribe)
        whenever(mockPostsRepository.getPostDetail(POST_ID)).thenReturn(getPostDetail)
        sut = PostActivityViewModel(mockPostsRepository)

        sut.setPost(POST)

        testObserver.assertSubscribed()
    }

    @Test
    fun `WHEN getPostDetail succeeds THAN postDetail is updated`() {
        val getPostDetail = Single.just(POST_DETAIL)
        whenever(mockPostsRepository.getPostDetail(POST_ID)).thenReturn(getPostDetail)
        sut = PostActivityViewModel(mockPostsRepository)

        sut.setPost(POST)

        assertEquals(POST_DETAIL, sut.postDetail.value)
    }

    @Test
    fun `WHEN getPostDetail succeeds THAN lading is false`() {
        val getPostDetail = Single.just(POST_DETAIL)
        whenever(mockPostsRepository.getPostDetail(POST_ID)).thenReturn(getPostDetail)
        sut = PostActivityViewModel(mockPostsRepository)

        sut.setPost(POST)

        assert(sut.loading.value == false)
    }

    companion object {
        private const val POST_ID = 1
        private val POST = Post(POST_ID, "test1")
        private val POST_DETAIL = PostDetail(POST, content = "", userName = "", comments = 0)
    }
}