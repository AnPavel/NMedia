package ru.netology.nmedia.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import java.io.IOException

class PostPagingSource(
    private val apiService: ApiService
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        Log.d("MyAppLog", "PostPagingSource * load: $$params")
        try {
            val result = when (params) {
                is LoadParams.Refresh -> apiService.getLatest(params.loadSize)
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )
                is LoadParams.Append -> apiService.getBefore(params.key, params.loadSize)

            }
            if (!result.isSuccessful) {
                throw ApiError(result.code(), result.message())

            }
            val body = result.body() ?: throw ApiError(
                result.code(),
                result.message(),
            )

            val nextKey = if (body.isEmpty()) null else body.last().id
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey,
            )

        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}
