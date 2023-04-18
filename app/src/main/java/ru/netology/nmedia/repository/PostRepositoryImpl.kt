package ru.netology.nmedia.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    //создаем объект OkHttpClient
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    //создаем парсер Gson сообщений
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    //список постов
    /*
    override fun getAll(): List<Post> {
        //создаем запрос
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }
    */

    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            //вызов через enqueue
            .enqueue(object : Callback {
                //
                override fun onResponse(call: Call, response: Response) {
                    //проверка на заполненное поле
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        //отлов ошибки
                        callback.onError(e)
                    }
                }
                //
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun likeById(post: Post, callback: PostRepository.GetAllCallback<Post>): Post {
        val request: Request = if (!post.likedByMe) {
            Request.Builder()
                .post("".toRequestBody())
        } else {
            Request.Builder()
                .delete()
        }
            .url("${BASE_URL}/api/slow/posts/${post.id}/likes")
            .build()

        //запрос на сервер
        /*
        client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, Post::class.java) }
        return post
        */
        client.newCall(request)
            //вызов через enqueue
            .enqueue(object : Callback {
                //
                override fun onResponse(call: Call, response: Response) {
                    //проверка на заполненное поле
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        //отлов ошибки
                        callback.onError(e)
                    }
                }

                //
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
        return post
    }

    override fun likeByShareId(id: Long) {
    }

    override fun likeByRedEyeId(id: Long) {
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun edit(post: Post) {
    }


    override fun removeById(id: Long, callback: PostRepository.GetAllCallback<Post>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })


    }
}
