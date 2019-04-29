package com.ablenesi.posts.feature.main

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.ablenesi.posts.R
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.robolectric.RobolectricTestRunner

@LargeTest
@RunWith(RobolectricTestRunner::class)
class MainActivityTest : AutoCloseKoinTest() {

    @get:Rule
    val rule = IntentsTestRule(MainActivity::class.java)

    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var mockMainViewModel: MainActivityViewModel
    private lateinit var posts: MutableLiveData<List<PostDetail>>
    private lateinit var loading: MutableLiveData<Boolean>

    @Before
    fun setUp() {
        posts = MutableLiveData()
        loading = MutableLiveData()
        mockMainViewModel = mock()
        whenever(mockMainViewModel.posts).thenReturn(posts)
        whenever(mockMainViewModel.loading).thenReturn(loading)
        loadKoinModules(module { viewModel(override = true) { mockMainViewModel } })

        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun `WHEN loading true THEN loading indicator is visible list is gone`() {
        loading.postValue(true)

        onView(withId(R.id.loading_indicator))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.posts_list))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun `WHEN loading false THEN loading indicator is gone list is visible`() {
        loading.postValue(false)

        onView(withId(R.id.loading_indicator))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.posts_list))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `WHEN posts are set THEN the UI displays them`() {
        val postValues = listOf(
            PostDetail(Post(1, "test1"),"content", "uresname", 10),
            PostDetail(Post(2, "test2"),"content", "uresname", 10)
        )

        posts.postValue(postValues)

        onView(withId(R.id.posts_list))
            .check(matches(atPosition(0, hasDescendant(withText("test1")))))
            .check(matches(atPosition(1, hasDescendant(withText("test2")))))
    }

    /*
    @Test
    fun `WHEN post is clicked THEN detail screen is opened`() {
        val title = "test1"
        val postValues = listOf(Post(0, title))
        posts.postValue(postValues)

        // TODO Figure out why do I get PerformException: Error performing ...
        // (IllegalArgumentException: expected one element but was: <android.view.ViewRootImpl@1d5bebf8, android.view.ViewRootImpl@60ab2ded>)

        onView(withId(R.id.posts_list))
            .perform(actionOnItem<DataBoundAdapter.DataBoundViewHolder<ItemPostBinding>>(withText(title), click()))

        intended(hasComponent(PostActivity::class.java.name))
    }
    */

    companion object {
        fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
            checkNotNull(itemMatcher)
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description?) {
                    description?.appendText("has item at position $position: ")
                    itemMatcher.describeTo(description)
                }

                override fun matchesSafely(view: RecyclerView): Boolean {
                    val viewHolder = view.findViewHolderForAdapterPosition(position)
                        ?: // has no item on such position
                        return false
                    return itemMatcher.matches(viewHolder.itemView)
                }
            }
        }
    }
}