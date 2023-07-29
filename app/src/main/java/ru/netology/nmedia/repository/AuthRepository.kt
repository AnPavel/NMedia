package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.dto.User

interface AuthRepository {

    suspend fun authenticateUser(login: String, pass: String) : User
    suspend fun registerUser(login: String, pass: String, name: String) : User
    suspend fun registerUserWithPhoto(login: String, pass: String, name: String, upload: MediaUpload): User
    suspend fun uploadMedia(upload: MediaUpload): Media
}
