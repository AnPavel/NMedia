package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val authorAvatar: String,
    val author: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val countShare: Int = 0,
    val countRedEye: Int = 0,
    val linkToVideo: String? = "",
    val hiddenEntry: Boolean,
    @Embedded
    val attachment: Attachment?,
) {
    fun toDto() = Post(
        id,
        authorId,
        authorAvatar,
        author,
        content,
        LocalDateTime.ofInstant(Instant.ofEpochSecond(published), ZoneId.systemDefault()),
        likedByMe,
        likes,
        countShare,
        countRedEye,
        linkToVideo,
        hiddenEntry,
        attachment
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.authorAvatar,
                dto.author,
                dto.content,
                dto.published.atZone(ZoneId.systemDefault()).toEpochSecond(),
                dto.likedByMe,
                dto.likes,
                dto.countShare,
                dto.countRedEye,
                dto.linkToVideo,
                dto.hiddenEntry,
                attachment = dto.attachment
            )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map{PostEntity.fromDto(it)}
