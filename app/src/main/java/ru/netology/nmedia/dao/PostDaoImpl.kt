package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.GetDataTime

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
            ${PostColumns.COLUMN_PUBLISHER} TEXT NOT NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_COUNT_FAVORITE} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_COUNT_SHARE} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_COUNT_REDEYE} INTEGER NOT NULL DEFAULT 0,
            ${PostColumns.COLUMN_COUNT_LINK_TO_VIDEO} TEXT NOT NULL
        );
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHER = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_COUNT_FAVORITE = "countFavorite"
        const val COLUMN_COUNT_SHARE = "countShare"
        const val COLUMN_COUNT_REDEYE = "countRedEye"
        const val COLUMN_COUNT_LINK_TO_VIDEO = "linkToVideo"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHER,
            COLUMN_LIKED_BY_ME,
            COLUMN_COUNT_FAVORITE,
            COLUMN_COUNT_SHARE,
            COLUMN_COUNT_REDEYE,
            COLUMN_COUNT_LINK_TO_VIDEO
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            put(PostColumns.COLUMN_AUTHOR, "Me")
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHER, "Now")
            //put(PostColumns.COLUMN_PUBLISHER, GetDataTime().dateFormat.toString())
        }
        val id = if (post.id != 0L) {
            db.update(
                PostColumns.TABLE,
                values,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(post.id.toString()),
            )
            post.id
        } else {
            db.insert(PostColumns.TABLE, null, values)
        }
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
           UPDATE posts SET
               countFavorite = countFavorite + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = ?;
        """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                publisher = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHER)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                countFavorite = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_COUNT_FAVORITE)),
                countRedEye = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_COUNT_SHARE)),
                countShare = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_COUNT_REDEYE)),
                linkToVideo = getString(getColumnIndexOrThrow(PostColumns.COLUMN_COUNT_LINK_TO_VIDEO)),
            )
        }
    }
}
