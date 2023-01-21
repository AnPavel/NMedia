package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.Post

class PostRepositoryInMemoryImplementation: PostRepository {

    private var post = Post(
        1, "Нетология. Университет интернет-профессий. Источник знаний для роста в профессии",
        "Нетология - российская компания и образовательная онлайн-платформа, запущенная в 2011 году. Одна из ведущих российских компаний онлайн-образования. Входит в IT-холдинг TalentTech, объединяющий компании по трём направлениям: EdTech, HRTech и Freelance.",
        "12 января 2023 в 12:00",
        false,
        999,
        20,
        300
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        post = post.copy(likedByMe = !post.likedByMe, countFavorite = post.countFavorite + if (post.likedByMe) -1 else 1)
        data.value = post
    }
    override fun likeShare() {
        post = post.copy(countShare = post.countShare + 100)
        data.value = post
    }
    override fun likeRedEye() {
        post = post.copy(countRedEye = post.countRedEye + 100)
        data.value = post
    }


}
