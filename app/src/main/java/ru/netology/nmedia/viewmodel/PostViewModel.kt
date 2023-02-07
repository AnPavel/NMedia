package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImplementation

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
    linkToVideo = null
)

class PostViewModel : ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImplementation()
    val data = repository.getAll()

    //текущий редактируемый пост
    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
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

    fun removeById(id: Long) = repository.removeById(id)

    fun cancel() {
        edited.value = empty
    }

    fun likeById(id: Long) = repository.likeById(id)
    //fun likeByShareId(id: Long) = repository.likeByShareId(id)
    fun likeByRedEyeId(id: Long) = repository.likeByRedEyeId(id)

}
