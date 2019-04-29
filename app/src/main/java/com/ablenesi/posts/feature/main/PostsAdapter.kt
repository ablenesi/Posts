package com.ablenesi.posts.feature.main

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.ablenesi.posts.R
import com.ablenesi.posts.core.model.Post
import com.ablenesi.posts.core.model.PostDetail
import com.ablenesi.posts.databinding.ItemPostBinding
import com.ablenesi.posts.feature.shared.DataBoundAdapter

class PostsAdapter(
    diffCallback: DiffUtil.ItemCallback<PostDetail>,
    onClick: (post: PostDetail, view: View) -> Unit
) : DataBoundAdapter<ItemPostBinding, PostDetail>(diffCallback) {

    init {
        setOnAdapterItemClickListener(object : DataBoundAdapter.AdapterItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                onClick(getItem(position), view)
            }
        })
    }

    override fun bindItem(
        holder: DataBoundAdapter.DataBoundViewHolder<ItemPostBinding>,
        position: Int,
        payloads: List<Any>
    ) {
        holder.binding.post = getItem(position)
    }

    override fun getItemViewType(position: Int): Int = R.layout.item_post
}