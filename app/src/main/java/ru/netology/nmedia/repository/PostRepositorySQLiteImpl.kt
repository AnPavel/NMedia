package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        val id = post.id
        val saved = dao.save(post)
        posts = if (id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map {
                if (it.id != id) it else saved
            }
        }
        data.value = posts
    }

    override fun edit(post: Post) {
        TODO("Not yet implemented")
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countFavorite = if (it.likedByMe) it.countFavorite - 1 else it.countFavorite + 1
            )
        }
        data.value = posts
    }

    override fun likeByShareId(id: Long) {
        dao.likeByShareId(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                countShare = it.countShare + 100
            )
        }
        data.value = posts
    }

    override fun likeByRedEyeId(id: Long) {
        dao.likeByRedEyeId(id)
        posts = posts.map {
            if (it.id != id) it else it.copy(
                countRedEye = it.countRedEye + 200
            )
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
        posts = posts.filter { it.id != id }
        data.value = posts
    }

}
