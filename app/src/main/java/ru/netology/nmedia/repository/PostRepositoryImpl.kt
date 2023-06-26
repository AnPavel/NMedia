package ru.netology.nmedia.repository

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
        /* адрес локального сервера - текущей машины с эмулятором */
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

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
        //создаем запрос
        val request: Request = Request.Builder()
            // медленные запросы с задержкой - slow
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                //успешный результат
                override fun onResponse(call: Call, response: Response) {
                    try {
                        //сохранить тело ответа
                        val body = response.body?.string() ?:  throw RuntimeException("body is null")
                        //вызов callback с параметром gson
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                // НЕ успешный результат
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

            })
    }

    override fun likeByIdAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
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
        client.newCall(request)
            .enqueue(object: Callback{
                override fun onResponse(call: Call, response: Response) {
                    val body = requireNotNull(response.body?.string()){"body is null"}
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetAllCallback<Unit>) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object: Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        callback.onError(Exception(response.message))
                    }
                    callback.onSuccess(Unit)
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetAllCallback<Post>) {
        val request = Request.Builder()
            .url("$BASE_URL/api/slow/posts")
            .post(gson.toJson(post).toRequestBody(jsonType))
            .build()

        client.newCall(request)
            .enqueue(object: Callback{
                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        callback.onError(Exception(response.message))
                    }
                    val body = requireNotNull(response.body?.string()){"body is null"}
                    callback.onSuccess(gson.fromJson(body, Post::class.java))
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


}
