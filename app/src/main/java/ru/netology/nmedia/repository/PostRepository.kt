package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {

    val data: Flow<PagingData<Post>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getAll()
    suspend fun getAllVisible()
    suspend fun showAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, model: PhotoModel)
    suspend fun uploadMedia(model: PhotoModel): Media
    suspend fun likeById(id: Long)
    suspend fun shareById(id: Long)
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
