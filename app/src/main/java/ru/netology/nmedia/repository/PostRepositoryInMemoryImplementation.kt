package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.GetDataTime

class PostRepositoryInMemoryImplementation : PostRepository {
    private var nextId = 1L

    private var posts = listOf(
        Post(
            nextId++,
            "Нетология-1. Университет интернет-профессий. Источник знаний для роста в профессии",
            "11 января 2023 11:00",
            "1- Нетология - российская компания и образовательная онлайн-платформа, запущенная в 2011 году. Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            999,
            10,
            300,
            ""
        ),
        Post(
            nextId++, "Нетология-2. Источник знаний для роста в профессии",
            "12 января 2023 12:00",
            "2- Нетология - Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            999,
            20,
            300,
            ""
        ),
        Post(
            nextId++, "Нетология-3. Источник знаний для роста в профессии",
            "13 января 2023 13:00",
            "3- Нетология - Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            99,
            30,
            700,
            ""
        ),
        Post(
            nextId++, "Нетология-4. Источник знаний для роста в профессии",
            "14 января 2023 14:00",
            "4- Нетология - Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
            false,
            99,
            40,
            900,
            ""
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun save(post: Post) {
        if (post.id == 0L) {
            //добавление нового поста
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    publisher = GetDataTime().dateFormat.toString(),
                    content = post.content,
                    likedByMe = false,
                    countFavorite = 0,
                    countShare = 0,
                    countRedEye = 0
                )
            ) + posts
        } else {
            //редактирование
            posts = posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
    }

    override fun edit(post: Post) {
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
    }

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

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
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
