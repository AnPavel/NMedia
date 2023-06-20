package ru.netology.nmedia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding

interface OnInteractionListener {
    fun onUrl(post: Post) {}
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onRedEye(post: Post) {}
    //fun onRefresh() {}
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
        binding.apply {
            textPoleAuthor.text = post.author
            textPolePublished.text = post.publisher
            textPoleHeading.text = post.content
            //imageFavorite.isChecked = post.likedByMe
            imageFavorite.text = "${post.countFavorite}"
            /*
            if (post.linkToVideo == "") {
                textPoleHeading.text = post.content
                textPoleUrl.setImageResource(0)
            } else {
                textPoleHeading.text = ""
                textPoleUrl.setImageResource(R.drawable.youtube240)
            }
             */
            //textPoleUrl.text = post.linkToVideo
            imageFavorite.text = transferToScreen(post.countFavorite)
            //textFavorite.text = transferToScreen(post.countFavorite)
            imageShare.text = transferToScreen(post.countShare)
            //textShare.text = transferToScreen(post.countShare)
            imageRedEye.text = transferToScreen(post.countRedEye)
            //textRedEye.text = transferToScreen(post.countRedEye)

            imageMenu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            R.id.menu_refresh -> {
                                Log.i("AAAA", "Refresh menu item selected")
                                //onInteractionListener.onRefresh()
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            textPoleUrl.setOnClickListener {
                onInteractionListener.onUrl(post)
            }

            imageFavorite.isChecked = post.likedByMe
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
