package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {

    //fun getAll(): List<Post>
    //fun likeById(id: Long)
    fun likeById(post: Post, callback: GetAllCallback<Post>): Post
    fun likeByShareId(id: Long)
    fun likeByRedEyeId(id: Long)
    //fun removeById(id: Long)
    fun removeById(id: Long, callback: GetAllCallback<Post>)
    fun save(post: Post)
    //fun save(post: Post, callback: GetAllCallback<Post>)
    fun edit(post: Post)

    fun getAllAsync(callback: GetAllCallback<List<Post>>)

    interface GetAllCallback<T> {
        fun onSuccess(postsDat: T) {}
        fun onError(e: Exception) {}
    }

}
