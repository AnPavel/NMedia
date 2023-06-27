package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val countShare: Int,
    val countRedEye: Int,
    val linkToVideo: String? = null
)
