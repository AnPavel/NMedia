package ru.netology.nmedia.dto

import ru.netology.nmedia.extens.AttachmentType

data class Post(
    val id: Long,
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
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
