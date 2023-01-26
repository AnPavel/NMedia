package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImplementation

class PostViewModel: ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemoryImplementation()
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun likeByShareId(id: Long) = repository.likeByShareId(id)
    fun likeByRedEyeId(id: Long) = repository.likeByRedEyeId(id)

}
