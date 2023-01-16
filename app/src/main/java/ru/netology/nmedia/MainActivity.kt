package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    var countShareTmp: Boolean = false
    var countShare: Int = 0
    var countRedEye: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dataLoadingOnScreen()
        binding.imageFavorite.setOnClickListener {
            clickOnScreenImageFavorite()
        }
        binding.imageShare.setOnClickListener {
            clickOnScreenImageShare()
        }
        binding.imageRedEye.setOnClickListener {
            clickOnScreenImageRedEye()
        }

    }

    private fun dataLoadingOnScreen() {
        val post = Post(
            1, "Нетология. Университет интернет-профессий. Источник знаний для роста в профессии",
            "Нетология - российская компания и образовательная онлайн-платформа, запущенная в 2011 году. Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            "12 января 2023 в 12:00",
            false,
            0,
            10,
            500
        )
        with(binding)
        {
            textPoleAuthor.text = post.author
            textPolePublished.text = post.publisher
            textPoleHeading.text = post.content
            textFavorite.text = post.countFavorite.toString()
            textShare.text = post.countShare.toString()
            textRedEye.text = post.countRedEye.toString()
        }
    }

    private fun clickOnScreenImageRedEye() {
        if (!countShareTmp) {
            countRedEye = binding.textRedEye.text.toString().toInt()
            countRedEye += 200
            binding.textRedEye.text = countRedEye.toString()
        } else {
            countRedEye += 200
            val countShareOnScreen = transferToScreen(countRedEye)
            when (countRedEye) {
                in 1..999 -> binding.textRedEye.text = countRedEye.toString()
                in 1000..9999 -> binding.textRedEye.text = countShareOnScreen
                in 10000..999999 -> binding.textRedEye.text = countShareOnScreen
                in 1000000..99999999 -> binding.textRedEye.text = countShareOnScreen
                else -> binding.textRedEye.text = "очень много"
            }
        }
        countShareTmp = true
    }

    private fun clickOnScreenImageShare() {
        if (!countShareTmp) {
            countShare = binding.textShare.text.toString().toInt()
            countShare += 100
            binding.textShare.text = countShare.toString()
        } else {
            countShare += 100
            val countShareOnScreen =
                (countShare / 1000).toString() + "." + ((countShare % 1000) / 100).toString() + "K"
            when (countShare) {
                in 1..999 -> binding.textShare.text = countShare.toString()
                in 1000..9999 -> binding.textShare.text = countShareOnScreen
                in 10000..999999 -> binding.textShare.text = (countShare / 1000).toString() + "K"
                in 1000000..999999999 -> binding.textShare.text =
                    (countShare / 1000000).toString() + "M"
                else -> binding.textShare.text = "очень много"
            }
        }
        countShareTmp = true
    }

    private fun clickOnScreenImageFavorite() {
        val countFavorite = binding.textFavorite.text.toString().toInt()
        if (countFavorite == 1) {
            binding.textFavorite.text = "0"
            binding.imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        } else {
            binding.textFavorite.text = "1"
            binding.imageFavorite.setImageResource(R.drawable.ic_baseline_favorite_24)
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
