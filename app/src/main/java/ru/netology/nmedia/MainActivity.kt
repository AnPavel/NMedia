package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val publisher: String,
    var likedByMe: Boolean = false,
    var countFavorite: Int,
    var countShare: Int,
    var countRedEye: Int
)

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            1, "Нетология. Университет интернет-профессий. Источник знаний для роста в профессии",
            "Нетология - российская компания и образовательная онлайн-платформа, запущенная в 2011 году. Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            "12 января 2023 в 12:00",
            false,
            109,
            20,
            300
        )
        with(binding)
        {
            textPoleAuthor.text = post.author
            textPolePublished.text = post.publisher
            textPoleHeading.text = post.content
            textFavorite.text = post.countFavorite.toString()
            textShare.text = post.countShare.toString()
            textRedEye.text = post.countRedEye.toString()
            if (post.likedByMe) {
                imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
            }

            binding.imageFavorite.setOnClickListener {
                post.likedByMe = !post.likedByMe
                imageFavorite.setImageResource(
                    if (post.likedByMe) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24
                )
                if (post.likedByMe) post.countFavorite++ else post.countFavorite--
                textFavorite.text = transferToScreen(post.countFavorite)
            }

            binding.imageShare.setOnClickListener {
                post.countShare+=100
                textShare.text = transferToScreen(post.countShare)
            }

            binding.imageRedEye.setOnClickListener {
                post.countRedEye+=200
                textRedEye.text = transferToScreen(post.countRedEye)
            }
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
