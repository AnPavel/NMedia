package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean,
    val likes: Int,
    val countShare: Int,
    val countRedEye: Int,
    val linkToVideo: String
)
