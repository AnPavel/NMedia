package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    fun getAll(): List<Post>
    //fun likeById(id: Long)
    fun likeById(post: Post): Post
    fun likeByShareId(id: Long)
    fun likeByRedEyeId(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun edit(post: Post)

    //асинхронный
    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        // если успешно в параметре - список постов
        fun onSuccess(post: List<Post>)
        // если ошибка
        fun onError()
    }

}
