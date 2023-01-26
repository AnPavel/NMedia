package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val publisher: String,
    val content: String,
    val likedByMe: Boolean = false,
    val countFavorite: Int,
    val countShare: Int,
    val countRedEye: Int
)
