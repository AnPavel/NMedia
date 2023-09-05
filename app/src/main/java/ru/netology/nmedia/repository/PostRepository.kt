package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {

    val data: Flow<PagingData<Post>>
    //val data: Flow<PagingData<FeedItem>>
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun getAll()
    suspend fun getAllVisible()
    suspend fun showAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, model: PhotoModel)
    suspend fun uploadMedia(model: PhotoModel): Media
    suspend fun likeById(id: Long)
    suspend fun shareById(id: Long)
    suspend fun removeById(id: Long)

}
