package ru.netology.nmedia.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    //список постов
    override fun getAll(): List<Post> {
        val posts = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()
            .let(client::newCall)
            .execute()
            .let { requireNotNull(it.body?.string()) {"body is null"} }
            .let { gson.fromJson(it, typeToken) }
        Log.e("myLog", "getALL: $posts", )
        return posts
    }

    override fun likeById(post: Post) {
        TODO("Not yet implemented")
    }
/*
    override fun likeById(id: Long) {
        //dao.likeById(id)
        TODO("Not yet implemented")
    }
*/
    override fun likeByShareId(id: Long) {
        //dao.likeByShareId(id)
        TODO("Not yet implemented")
    }

    override fun likeByRedEyeId(id: Long) {
        //dao.likeByRedEyeId(id)
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()

    }

}
