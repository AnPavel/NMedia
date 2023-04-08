package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

//пустой пост
private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    publisher = "",
    countFavorite = 0,
    countShare = 0,
    countRedEye = 0,
    linkToVideo = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryImpl()
    //private val repository: PostRepository = PostRepositorySQLiteImpl(AppDb.getInstance(application).postDao)
    //private val repository: PostRepository = PostRepositoryInMemoryImplementation()
    //private val repository: PostRepository = PostRepositoryFileImpl(application)
    //private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)

    private val _data = MutableLiveData(FeedModel())

    //список постов
    val data: LiveData<FeedModel>
        get() = _data

    //текущий пост
    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        thread {
            // Начинаем загрузку
            _data.postValue(FeedModel(loading = true))
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (e: IOException) {
                // Получена ошибка
                FeedModel(error = true)
            }.also(_data::postValue)
        }
    }

    fun save() {
        edited.value?.let {
            thread {
                repository.save(it)
                _postCreated.postValue(Unit)
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long, post: Post) {
        thread {
            try {
                val post = repository.likeById(post)
                val posts = _data.value?.posts.orEmpty().map {
                    if (it.id == id) {
                        post
                    } else {
                        it
                    }
                }
                _data.postValue(_data.value?.copy(posts = _data.value?.posts.orEmpty()))
            } catch (e: IOException) {
                println(e.message.toString())
            }
        }
    }

    fun likeByShareId(id: Long) {
        thread { repository.likeByShareId(id) }
    }

    fun likeByRedEyeId(id: Long) {
        thread { repository.likeByRedEyeId(id) }
    }

    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }
}
