package ru.netology.nmedia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.listener.OnInteractionListener
import ru.netology.nmedia.extens.load
import ru.netology.nmedia.extens.loadCircle
import ru.netology.nmedia.utils.*


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
        binding.apply {
            postAvatar.loadCircle("${BuildConfig.BASE_URL}avatars/${post.authorAvatar}")
            attachment.load("${BuildConfig.BASE_URL}media/${post.attachment?.url}")
            attachment.isVisible = !post.attachment?.url.isNullOrBlank()
            textPoleAuthor.text = post.author
            textPolePublished.text = post.published
            textPoleHeading.text = post.content
            imageFavorite.isChecked = post.likedByMe
            imageFavorite.text = "${post.likes}"

            linkToVideo.text = post.linkToVideo
            linkToVideo.isVisible = false
            if (linkToVideo.text == "") videoGroup.isVisible = false

            imageFavorite.text = transferToScreen(post.likes)
            imageShare.text = transferToScreen(post.countShare)
            imageRedEye.text = transferToScreen(post.countRedEye)

            //показать меню если я автор
            imageMenu.isVisible = post.ownedByMe

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
                Log.d("MyAppLog", "PostAdapter * onPlayVideo: $post")
                onInteractionListener.onPlayVideo(post)
            }

            textPoleHeading.setOnClickListener {
                Log.d("MyAppLog", "PostAdapter * onOpenPost: $post")
                onInteractionListener.onOpenPost(post)
            }

            attachment.setOnClickListener {
                Log.d("MyAppLog", "PostAdapter * onShowAttachment: $post")
                onInteractionListener.onShowAttachment(post)
            }

            imageFavorite.setOnClickListener {
                Log.d("MyAppLog", "PostAdapter * onLike: $post")
                onInteractionListener.onLike(post)
            }

            imageShare.setOnClickListener {
                Log.d("MyAppLog", "PostAdapter * onShare: $post")
                onInteractionListener.onShare(post)
            }

            imageRedEye.setOnClickListener {
                Log.d("MyAppLog", "PostAdapter * onRedEye: $post")
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
