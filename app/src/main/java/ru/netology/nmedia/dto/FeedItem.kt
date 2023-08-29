package ru.netology.nmedia.dto

import ru.netology.nmedia.extens.AttachmentType

sealed interface FeedItem {
    val id: Long
}

data class Ad(
    override val id: Long,
    val url: String,
    val image: String,
): FeedItem

data class Post(
    override val id: Long,
    val authorId: Long,
    val authorAvatar: String = "",
    val author: String = "",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val countShare: Int,
    val countRedEye: Int,
    val linkToVideo: String?,
    val hiddenEntry: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
): FeedItem

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
