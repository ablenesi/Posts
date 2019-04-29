package com.ablenesi.posts.feature.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.ablenesi.posts.R
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.ablenesi.posts.databinding.ActivityMainBinding
import com.ablenesi.posts.feature.detail.PostActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val adapter = PostsAdapter(object : DiffUtil.ItemCallback<PostDetail>() {
            override fun areItemsTheSame(oldItem: PostDetail, newItem: PostDetail): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: PostDetail, newItem: PostDetail): Boolean = oldItem == newItem
        }) { post, view ->
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, post.title)
            startActivity(PostActivity.getStartIntent(this@MainActivity, post), options.toBundle())
        }

        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel
        binding.postsList.adapter = adapter
        binding.postsList.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mainViewModel.posts.observe(this, Observer { adapter.submitList(it) })
    }
}
