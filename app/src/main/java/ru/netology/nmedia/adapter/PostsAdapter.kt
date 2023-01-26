package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding

typealias OnLikeListener = (post: Post) -> Unit

class PostsAdapter(
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnLikeListener,
    private val onRedEyeListener: OnLikeListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onLikeListener, onShareListener, onRedEyeListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onLikeListener: OnLikeListener,
    private val onShareListener: OnLikeListener,
    private val onRedEyeListener: OnLikeListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            textPoleAuthor.text = post.author
            textPolePublished.text = post.publisher
            textPoleHeading.text = post.content
            if (post.likedByMe) imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_24) else imageFavorite.setImageResource(
                R.drawable.ic_baseline_favorite_border_24
            )
            textFavorite.text = transferToScreen(post.countFavorite)
            textShare.text = transferToScreen(post.countShare)
            textRedEye.text = transferToScreen(post.countRedEye)
            imageFavorite.setOnClickListener {
                onLikeListener(post)
            }
            imageShare.setOnClickListener {
                onShareListener(post)
            }
            imageRedEye.setOnClickListener {
                onRedEyeListener(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

private fun transferToScreen(count: Int): String {
    val formatCount = when {
        count in 1000..9999 -> {
            String.format("%.1fK", count / 1000.0)
        }
        count in 10000..999999 -> {
            String.format("%dK", count / 1000)
        }
        count > 1000000 -> {
            String.format("%.1fM", count / 1000000.0)
        }
        else -> {
            count.toString()
        }
    }
    return formatCount
}
