package com.ablenesi.posts.feature.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ablenesi.posts.R
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.databinding.ActivityPostBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostActivity : AppCompatActivity() {

    private val postViewModel: PostActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityPostBinding = DataBindingUtil.setContentView(this, R.layout.activity_post)
        postViewModel.setPost(intent.getPost())
        binding.lifecycleOwner = this
        binding.viewModel = postViewModel

        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        private const val TITLE_EXTRA = "title"
        private const val ID_EXTRA = "id"

        fun getStartIntent(context: Context, post: Post): Intent? {
            return Intent(context, PostActivity::class.java)
                .putExtra(TITLE_EXTRA, post.title)
                .putExtra(ID_EXTRA, post.id)
        }

        private fun Intent.getPost(): Post = Post(getIntExtra(ID_EXTRA, 0), getStringExtra(TITLE_EXTRA))
    }
}

