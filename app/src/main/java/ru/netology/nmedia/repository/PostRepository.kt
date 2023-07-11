package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {

    val data: Flow<List<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun likeById(id: Long)
    suspend fun removeById(id: Long)


    /*
    //синхронный
    fun getAll(): List<Post>
    //fun likeById(id: Long)
    fun likeById(post: Post): Post
    fun likeByShareId(id: Long)
    fun likeByRedEyeId(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun edit(post: Post)
    */

    /*
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
    */

}
