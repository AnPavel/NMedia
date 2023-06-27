package ru.netology.nmedia.dto

import ru.netology.nmedia.extens.AttachmentType

data class Post(
    val id: Long,
    val authorAvatar: String = "",
    val author: String = "77777777777",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val countShare: Int,
    val countRedEye: Int,
    val linkToVideo: String?,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)
