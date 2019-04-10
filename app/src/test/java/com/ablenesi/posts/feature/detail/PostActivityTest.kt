package com.ablenesi.posts.feature.detail

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import com.ablenesi.posts.PostsApplication
import com.ablenesi.posts.R
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test

import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.robolectric.RobolectricTestRunner

@LargeTest
@RunWith(RobolectricTestRunner::class)
class PostActivityTest : AutoCloseKoinTest() {

    private lateinit var scenario: ActivityScenario<PostActivity>
    private lateinit var mockPostViewModel: PostActivityViewModel

    private lateinit var loading: MutableLiveData<Boolean>
    private lateinit var title: MutableLiveData<String>
    private lateinit var postDetail: MutableLiveData<PostDetail>

    @Before
    fun setUp() {
        loading = MutableLiveData()
        title = MutableLiveData()
        postDetail = MutableLiveData()

        mockPostViewModel = mock()
        whenever(mockPostViewModel.loading).thenReturn(loading)
        whenever(mockPostViewModel.title).thenReturn(title)
        whenever(mockPostViewModel.postDetail).thenReturn(postDetail)

        loadKoinModules(module { viewModel(override = true) { mockPostViewModel } })
        scenario = ActivityScenario.launch(
            PostActivity.getStartIntent(
                getApplicationContext<PostsApplication>(),
                Post(ID, TITLE)
            )
        )
    }

    @Test
    fun `WHEN screen is opened THEN title is set`() {
        title.value = TITLE

        onView(withId(R.id.title))
            .check(matches(withText(TITLE)))
    }

    @Test
    fun `WHEN loading is true THEN loading indicator is visible content is gone`() {
        loading.value = true

        onView(withId(R.id.loading_indicator))
            .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.content_group))
            .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun `WHEN loading is false THEN loading indicator is gone content is visible`() {
        loading.value = false

        onView(withId(R.id.loading_indicator))
            .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.content_group))
            .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun `WHEN loading is false and postDetail is set THEN information is displayed`() {
        loading.value = false
        postDetail.value = PostDetail(Post(ID, TITLE), content = CONTENT, userName = USERNAME, comments = COMMENTS)

        onView(withId(R.id.content))
            .check(matches(withText(CONTENT)))
        onView(withId(R.id.username))
            .check(matches(withText("by $USERNAME")))
        onView(withId(R.id.comments))
            .check(matches(withText("$COMMENTS comments")))
    }

    companion object {
        private const val TITLE = "title"
        private const val USERNAME = "username"
        private const val CONTENT = "body"
        private const val COMMENTS = 10
        private const val ID = 1
    }
}