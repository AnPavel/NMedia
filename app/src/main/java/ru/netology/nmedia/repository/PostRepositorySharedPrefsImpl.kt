package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.GetDataTime

class PostRepositorySharedPrefsImpl(
    context: Context,
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            nextId = posts.maxOfOrNull { it.id }?.inc() ?: 1
            data.value = posts
        }
    }

    //override fun getAll(): LiveData<List<Post>> = data
    //временное решение при изменении файла PostRepositoryImpl  - > Изменили LiveData на List
    override fun getAll(): List<Post> {
        return emptyList()
    }

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
                    countRedEye = 0,
                    linkToVideo = ""
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

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                countFavorite = if (it.likedByMe) it.countFavorite -1 else it.countFavorite + 1
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
    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }
}
