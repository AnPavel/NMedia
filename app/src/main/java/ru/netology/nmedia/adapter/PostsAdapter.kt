package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.extens.load
import ru.netology.nmedia.extens.loadCircle

interface OnInteractionListener {
    fun onUrl(post: Post) {}
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onRedEye(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {

        val baseUrl = "http://10.0.2.2:9999"

        binding.apply {
            postAvatar.loadCircle("$baseUrl/avatars/${post.authorAvatar}")
            attachment.load("$baseUrl/images/${post.attachment?.url}")
            attachment.contentDescription = post.attachment?.description
            attachment.isVisible = !post.attachment?.url.isNullOrBlank()
            textPoleAuthor.text = post.author
            textPolePublished.text = post.published
            textPoleHeading.text = post.content
            imageFavorite.isChecked = post.likedByMe
            imageFavorite.text = "${post.likes}"
 /*
            if (linkToVideo.text == "") videoGroup.isVisible = false

            linkToVideo.isVisible = false
 */
            imageFavorite.text = transferToScreen(post.likes)
            imageShare.text = transferToScreen(post.countShare)
            imageRedEye.text = transferToScreen(post.countRedEye)

            imageMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            linkToVideo.setOnClickListener {
                onInteractionListener.onUrl(post)
            }

            imageFavorite.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            imageShare.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            imageRedEye.setOnClickListener {
                onInteractionListener.onRedEye(post)
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
