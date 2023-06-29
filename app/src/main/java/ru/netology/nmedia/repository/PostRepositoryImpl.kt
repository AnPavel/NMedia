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
    /*
    override fun getAll(): List<Post> {
        //создаем запрос
        val posts = Request.Builder()
            // медленные запросы с задержкой - slow
            .url("${BASE_URL}/api/slow/posts")
            .build()
            // вызываем клиент и передаем запрос
            .let(client::newCall)
            .execute()
            // обрабатываем ответ на наш запрос через метод let  - извлекаем ответ в виде строки
            .let { requireNotNull(it.body?.string()) { "body is null" } }
            // через библиотеку gson получем список постов
            .let { gson.fromJson(it, typeToken) }
        Log.e("myLog", "getALL: $posts")
        return posts
    }
    */

    // АСИНХРОННЫЙ метод
    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        PostApi.service
            .getPosts()
            .enqueue(object : Callback<List<Post>> {

                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }

            })
    }

    override fun likeByIdAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        PostApi.service
            .likeById(post.id)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    override fun unlikeByIdAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        PostApi.service
            .unlikeById(post.id)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetAllCallback<Unit>) {
        PostApi.service
            .deletePost(id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        PostApi.service
            .savePost(post)
            .enqueue(object : Callback<Post> {

                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                    }
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

}
