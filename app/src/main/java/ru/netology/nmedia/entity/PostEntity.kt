package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val countShare: Int = 0,
    val countRedEye: Int = 0,
    val linkToVideo: String? = "",
    val hiddenEntry: Boolean,
    val attachment: String = "",
) {
    fun toDto() = Post(
        id,
        authorAvatar,
        author,
        content,
        published,
        likedByMe,
        likes,
        countShare,
        countRedEye,
        linkToVideo,
        hiddenEntry
    )

    companion object {
        fun fromDto(dto: Post, hiddenEntry: Boolean) =
            PostEntity(
                dto.id,
                dto.authorAvatar,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.countShare,
                dto.countRedEye,
                dto.linkToVideo,
                dto.hiddenEntry,
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(hiddenEntry: Boolean): List<PostEntity> = map{PostEntity.fromDto(it, hiddenEntry)}
