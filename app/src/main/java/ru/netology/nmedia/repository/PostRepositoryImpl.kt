package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.extens.AttachmentType
import ru.netology.nmedia.error.*
import java.io.IOException
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostApiService
    ) : PostRepository {
    override val data: Flow<List<Post>> = postDao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            //ждем 10 сек
            delay(10_000L)
            //делаем запрос
            val response = PostApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            //обработка
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            //вставляем в базу
            postDao.insert(body.toEntity(hiddenEntry = true))
            //выдаем подписчикам кол-во новых постов
            emit(body.size)
        }
    }
        .flowOn(Dispatchers.Default)


    override suspend fun getAll() {
        try {
            val response = PostApi.service.getPosts()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(hiddenEntry = false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    //вывести посты без скрытых - новых
    override suspend fun getAllVisible() {
        try {
            val response = PostApi.service.getPosts()
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            val posts = response.body() ?: throw RuntimeException("body is null")
            postDao.insert(posts.map { PostEntity.fromDto(it, hiddenEntry = false) })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    //вывести все посты после изменения флага видимости
    override suspend fun showAll() {
        try {
            postDao.showAll()
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
            postDao.insert(PostEntity.fromDto(body, hiddenEntry = false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun saveWithAttachment(post: Post, model: PhotoModel) {
        try {
            val media = uploadMedia(model)

            val response = PostApi.service.savePost(
                post.copy(
                    attachment =
                    Attachment(
                        media.id,
                        AttachmentType.IMAGE
                    )
                )
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, hiddenEntry = false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }


    override suspend fun uploadMedia(model: PhotoModel): Media {
        val response = PostApi.service.uploadMedia(
            MultipartBody.Part.createFormData("file", "file", model.file!!.asRequestBody())
        )
        if (!response.isSuccessful) {
            throw ApiError(response.code(), response.message())
        }
        return requireNotNull(response.body())
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
                    postDao.insert(PostEntity.fromDto(body, hiddenEntry = false))
                } else {
                    PostApi.service.likeById(id)
                    val body = (response.body())?.copy(
                        likedByMe = true,
                        likes = (response.body())!!.likes + 1
                    ) ?: throw ApiError(response.code(), response.message())
                    postDao.insert(PostEntity.fromDto(body, hiddenEntry = false))
                }
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun shareById(id: Long) {
        postDao.shareById(id)
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
