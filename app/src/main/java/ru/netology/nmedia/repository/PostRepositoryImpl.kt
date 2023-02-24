package ru.netology.nmedia.repository

import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {
    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            it.toDto()
        }
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun likeByShareId(id: Long) {
        TODO("Not yet implemented")
    }

    override fun likeByRedEyeId(id: Long) {
        TODO("Not yet implemented")
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun edit(post: Post) {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

}
