package ru.netology.nmedia.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.SocketTimeoutError
import java.io.IOException


class PostPagingSource(
    private val apiService: ApiService,
) : PagingSource<Long, Post>() {

    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        Log.d("MyAppLog", "PostPagingSource * getRefreshKey: $state")
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        Log.d("MyAppLog", "PostPagingSource * load: $params")
        try {
            val response = when (params) {
                is LoadParams.Refresh -> apiService.getLatest(params.loadSize)
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )
                is LoadParams.Append -> apiService.getBefore(params.key, params.loadSize)
            }

            if (!response.isSuccessful) {
                Log.e("MyAppLog", "PostPagingSource * ERROR: $response")
                throw ApiError(
                    response.code(),
                    response.message()
                )
            }

            Log.d("MyAppLog", "PostPagingSource * body: ${response.body()}")
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            val nextKey = if (body.isEmpty()) null else body.last().id
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey,
            )

        } catch (e: IOException) {
            Log.e("MyAppLog", "PostPagingSource * IOException: $e")
            return LoadResult.Error(e)
        } catch (e: SocketTimeoutError) {
            Log.e("MyAppLog", "PostPagingSource * SocketTimeoutError: $e")
            return LoadResult.Error(e)
        } catch (e: InterruptedException) {
            Log.e("MyAppLog", "PostPagingSource * InterruptedException: $e")
            return LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("MyAppLog", "PostPagingSource * Exception: $e")
            return LoadResult.Error(e)
        }

    }
}
