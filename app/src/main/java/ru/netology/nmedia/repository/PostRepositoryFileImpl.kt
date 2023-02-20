package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.GetDataTime

class PostRepositoryFileImpl(
    private val context: Context,
) : PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            //если файл есть - читаем
            context.openFileInput(filename).bufferedReader().use {
                posts = gson.fromJson(it,type)
                /*добавить максимальный из id*/
                nextId = posts.maxOfOrNull { it.id }?.inc() ?: 1
                data.value = posts
            }
        } else {
            //если нет, записываем пустой массив
            sync()
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeByShareId(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(countShare = it.countShare + 100)
        }
        data.value = posts
        sync()
    }

    override fun likeByRedEyeId(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(countRedEye = it.countRedEye + 200)
        }
        data.value = posts
        sync()
    }

    override fun edit(post: Post) {
        TODO("Not yet implemented")
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countFavorite = if (it.likedByMe) it.countFavorite - 1 else it.countFavorite + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            //добавление нового поста
            posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    publisher = GetDataTime().dateFormat.toString(),
                    content = post.content,
                    likedByMe = false,
                    countFavorite = 0,
                    countShare = 0,
                    countRedEye = 0
                )
            ) + posts
            data.value = posts
            sync()
            return
        }

        //редактирование
        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }

}
