package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorAvatar: String,
    val author: String,
    val published: String,
    val content: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val countShare: Int = 0,
    val countRedEye: Int = 0,
    val linkToVideo: String = "",
    val attachment: String = "",
) {
    fun toDto() = Post(
        id,
        authorAvatar,
        author,
        published,
        content,
        likedByMe,
        likes,
        countShare,
        countRedEye,
        linkToVideo,
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorAvatar,
                dto.author,
                dto.publisher,
                dto.content,
                dto.likedByMe,
                dto.likes,
                dto.countShare,
                dto.countRedEye,
                dto.linkToVideo,
            )
    }
}
