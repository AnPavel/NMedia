package ru.netology.nmedia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.listener.OnInteractionListener
import ru.netology.nmedia.extens.load
import ru.netology.nmedia.extens.loadCircle
import ru.netology.nmedia.utils.*


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    //получаем тип элемента
    override fun getItemViewType(position: Int): Int =
        //получаем позицию элемента для определения типа
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type")
        }

    //создаем
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            else -> error("unknown view type: $viewType")
        }

    //заполнение данными
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //в зависимости от типа элемента
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

//класс для рекламы
class AdViewHolder(
    private val binding: CardAdBinding,
): RecyclerView.ViewHolder(binding.root) {
    //заполняем карточку с рекламой
    fun bind(ad: Ad) {
        binding.imageAd.load("${BuildConfig.BASE_URL}media/${ad.image}")   //загрузка картинки с помощью Glide
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

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        //добавили проверку - сравнить рекламу и пост и у них совпали id
        if (oldItem::class != newItem::class) {  //проверяем классы элементов
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
