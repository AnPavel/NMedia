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
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardDateBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.DateSeparator
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.extens.loadCircle
import ru.netology.nmedia.utils.transferToScreen
import ru.netology.nmedia.view.load


class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(FeedItemDiffCallback()) {
    private val typeAd = 0
    private val typePost = 1
    private val typeDate = 2

    interface OnInteractionListener {
        fun onPlayVideo(post: Post) {}
        fun onOpenPost(post: Post) {}
        fun onShowAttachment(post: Post) {}
        fun onLike(post: Post) {}
        fun onEdit(post: Post) {}
        fun onRemove(post: Post) {}
        fun onShare(post: Post) {}
        fun onRedEye(post: Post) {}
        fun onAdClick(ad: Ad) {}
    }


    //получаем тип элемента
    override fun getItemViewType(position: Int): Int {
        Log.d("MyAppLog", "FeedAdapter * getItemViewType: ${position}  / ${getItem(position)}")
        //получаем позицию элемента для определения типа
        return when (getItem(position)) {
            is Ad -> typeAd
            is Post -> typePost
            is DateSeparator -> typeDate
            null -> error("unknown item type")
        }
    }


    //создаем
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("MyAppLog", "FeedAdapter * onCreateViewHolder: ${viewType}")
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            typeAd -> AdViewHolder(
                CardAdBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )

            typePost -> PostViewHolder(
                CardPostBinding.inflate(layoutInflater, parent, false),
                onInteractionListener
            )

            typeDate -> DateViewHolder(
                CardDateBinding.inflate(layoutInflater, parent, false),
            )

            else -> error("unknown view type: $viewType")
        }
    }


    //заполнение данными
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("MyAppLog", "FeedAdapter * onBindViewHolder: ${getItem(position)}")
        //в зависимости от типа элемента
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is DateSeparator -> (holder as? DateViewHolder)?.bind(item)
            null -> error("unknown item type: $item")
        }
    }


    //класс для рекламы
    class AdViewHolder(
        private val binding: CardAdBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        //заполняем карточку с рекламой
        fun bind(ad: Ad) {
            Log.d("MyAppLog", "FeedAdapter * AdViewHolder: реклама")
            binding.imageAd.load("${BuildConfig.BASE_URL}media/${ad.image}")   //загрузка картинки с помощью Glide
            binding.imageAd.setOnClickListener {
                onInteractionListener.onAdClick(ad)
            }
        }
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onInteractionListener: OnInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            Log.d("MyAppLog", "FeedAdapter * PostViewHolder: пост / дата: ${post.published}")
            binding.apply {
                postAvatar.loadCircle("${BuildConfig.BASE_URL}avatars/${post.authorAvatar}")
                attachment.load("${BuildConfig.BASE_URL}media/${post.attachment?.url}")
                attachment.isVisible = !post.attachment?.url.isNullOrBlank()
                textPoleAuthor.text = post.author
                textPolePublished.text = post.published.toString()
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
                    Log.d("MyAppLog", "FeedAdapter * onPlayVideo: $post")
                    onInteractionListener.onPlayVideo(post)
                }

                textPoleHeading.setOnClickListener {
                    Log.d("MyAppLog", "FeedAdapter * onOpenPost: $post")
                    onInteractionListener.onOpenPost(post)
                }

                attachment.setOnClickListener {
                    Log.d("MyAppLog", "FeedAdapter * onShowAttachment: $post")
                    onInteractionListener.onShowAttachment(post)
                }

                imageFavorite.setOnClickListener {
                    Log.d("MyAppLog", "FeedAdapter * onLike: $post")
                    onInteractionListener.onLike(post)
                }

                imageShare.setOnClickListener {
                    Log.d("MyAppLog", "FeedAdapter * onShare: $post")
                    onInteractionListener.onShare(post)
                }

                imageRedEye.setOnClickListener {
                    Log.d("MyAppLog", "FeedAdapter * onRedEye: $post")
                    onInteractionListener.onRedEye(post)
                }

            }
        }
    }

    class DateViewHolder(
        private val binding: CardDateBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(date: DateSeparator) {
            Log.d("MyAppLog", "FeedAdapter * DateViewHolder: разделитель по датам / ${date.type}")
            val resource = when (date.type) {
                DateSeparator.Type.TODAY -> R.string.today
                DateSeparator.Type.YESTERDAY -> R.string.yesterday
                DateSeparator.Type.WEEK_AGO -> R.string.week_ago
            }

            binding.root.setText(resource)
        }
    }

    class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
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
}
