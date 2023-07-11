package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.*
import java.io.IOException
import java.util.concurrent.CancellationException


class PostRepositoryImpl(private val postDao: PostDao) : PostRepository {
    override val data: Flow<List<Post>> = postDao.getAll().map(List<PostEntity>::toDto)

    override fun getNewerCount(id: Long): Flow<Int> = flow{
        while (true) {
            try {
                //ждем 10 сек
                delay(10_000)
                //делаем запрос
                val response = PostApi.service.getNewer(id)
                //обработка либо посты либо пусто
                val posts = response.body().orEmpty()
                //вставляем в базу
                postDao.insert(posts.toEntity())
                //выдаем подписчикам кол-во новых постов
                emit(posts.size)
            } catch (e: CancellationException) {
                throw e
            }
            catch (e: Exception) {
                //ignore
                e.printStackTrace()
            }
        }
    }

    override suspend fun getAll() {
        try {
            val response = PostApi.service.getPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.service.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = PostApi.service.getById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                postDao.likeById(id)
                if (response.body()!!.likedByMe) {
                    PostApi.service.unlikeById(id)
                    val body = (response.body())?.copy(
                        likedByMe = false,
                        likes = (response.body())!!.likes - 1
                    ) ?: throw ApiError(response.code(), response.message())
                    postDao.insert(PostEntity.fromDto(body))
                } else {
                    PostApi.service.likeById(id)
                    val body = (response.body())?.copy(
                        likedByMe = true,
                        likes = (response.body())!!.likes + 1
                    ) ?: throw ApiError(response.code(), response.message())
                    postDao.insert(PostEntity.fromDto(body))
                }
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostApi.service.deletePost(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                postDao.removeById(id)
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}

    /*
    // СИНХРОННЫЙ метод
    // формируем запрос на список постов - результат список постов

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


    // АСИНХРОННЫЙ метод
    override fun getAllAsync(callback: PostRepository.GetAllCallback<List<Post>>) {
        PostApi.service
            .getPosts()
            .enqueue(object : Callback<List<Post>> {

                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    //Returns true if code() is in the range (200..300)
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.errorBody()?.string()))
                        //response.code()
                        return
                    }
                    //можно сделать проверку на код от 201 до 300
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
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
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
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
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
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
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
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
                        return
                    }

                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }

            })
    }

    */
