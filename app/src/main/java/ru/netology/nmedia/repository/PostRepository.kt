package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    /*синхронный
    fun getAll(): List<Post>
    //fun likeById(id: Long)
    fun likeById(post: Post): Post
    fun likeByShareId(id: Long)
    fun likeByRedEyeId(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun edit(post: Post)
    */
    fun getAll(): List<Post>

    //асинхронный
    fun getAllAsync(callback: GetAllCallback<List<Post>>)
    fun likeByIdAsync(post: Post, callback: GetAllCallback<Post>)
    fun unlikeByIdAsync(post: Post, callback: GetAllCallback<Post>)
    fun removeByIdAsync(id: Long, callback: GetAllCallback<Unit>)
    fun saveAsync(post: Post, callback: GetAllCallback<Post>)

    interface GetAllCallback<T> {
        fun onSuccess(value: T)     // если успешно в параметре - список постов
        fun onError(e: Exception)   // если ошибка
    }

}
