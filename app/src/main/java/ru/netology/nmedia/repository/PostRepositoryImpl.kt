package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
//import java.io.IOException
import java.lang.Exception


class PostRepositoryImpl : PostRepository {

    // СИНХРОННЫЙ метод
    // формируем запрос на список постов - результат список постов
    override fun getAll(): List<Post> {
        return PostApi.service.getPosts()
            .execute()
            .let {
                //проверка на успешный ответ
                if (!it.isSuccessful){
                    error("Response code is ${it.code()}")
                }
                it.body() ?: throw RuntimeException("body is null")
            }
    }

    // АСИНХРОННЫЙ метод
    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        PostApi.service
            .getPosts()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        return
                    }

                    val posts = response.body()
                    if (posts == null) {
                        callback.onError(RuntimeException("Body is empty"))
                        return
                    }

                    callback.onSuccess(posts)
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun likeByIdAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        //PostApi.service.likeById(post)
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetAllCallback<Unit>) {
        PostApi.service.deletePost(id)
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        PostApi.service.savePost(post)
            .execute()
    }

}
