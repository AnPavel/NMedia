package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImplementation : PostRepository {
    private var posts = listOf(
        Post(
            1, "Нетология-1. Университет интернет-профессий. Источник знаний для роста в профессии",
            "10 января 2023 в 10:00",
            "Нетология - российская компания и образовательная онлайн-платформа, запущенная в 2011 году. Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            999,
            10,
            300
        ),
        Post(
            2, "Нетология-2. Источник знаний для роста в профессии",
            "12 января 2023 в 12:00",
            "Нетология - Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            999,
            20,
            300
        ),
        Post(
            3, "Нетология-3. Источник знаний для роста в профессии",
            "13 января 2023 в 13:00",
            "Нетология - Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            99,
            30,
            700
        ),
        Post(
            4, "Нетология-4. Источник знаний для роста в профессии",
            "14 января 2023 в 14:00",
            "Нетология - Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            99,
            40,
            900
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) {
                it
            } else {
                if (it.likedByMe) {
                    it.copy(
                        likedByMe = !it.likedByMe,
                        countFavorite = it.countFavorite - 1
                    )
                } else {
                    it.copy(
                        likedByMe = !it.likedByMe,
                        countFavorite = it.countFavorite + 1
                    )
                }
            }
        }
        data.value = posts
    }

    override fun likeByShareId(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(countShare = it.countShare + 100)
        }
        data.value = posts
    }

    override fun likeByRedEyeId(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(countRedEye = it.countRedEye + 200)
        }
        data.value = posts
    }
}
