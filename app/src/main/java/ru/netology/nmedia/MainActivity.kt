package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val publisher: String,
    val likedByMe: Boolean = false,
    val countFavorite: Int,
    val countShare: Int,
    val countRedEye: Int
)

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>()
        viewModel.data.observe(this) { post ->

            with(binding)
            {
                textPoleAuthor.text = post.author
                textPolePublished.text = post.publisher
                textPoleHeading.text = post.content
                textFavorite.text = transferToScreen(post.countFavorite)
                textShare.text = transferToScreen(post.countShare)
                textRedEye.text = transferToScreen(post.countRedEye)
                if (post.likedByMe) {
                    imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
            }
        }
        binding.imageFavorite.setOnClickListener {
            viewModel.like()
        }
        binding.imageShare.setOnClickListener {
            viewModel.likeShare()
        }
        binding.imageRedEye.setOnClickListener {
            viewModel.likeRedEye()
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

}
