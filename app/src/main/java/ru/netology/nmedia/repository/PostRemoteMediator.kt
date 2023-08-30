package ru.netology.nmedia.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError


@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val appDb: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    ) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            Log.d("MyAppLog", "PostRemoteMediator * LoadType ->: $loadType")
            val response = when (loadType) {
                //LoadType.REFRESH -> postRemoteKeyDao.max()?.let {
                //    Log.d("MyAppLog", "PostRemoteMediator * LoadType.REFRESH: ${state.config.initialLoadSize}")
                //    apiService.getAfter(it, state.config.pageSize)
                //} ?: apiService.getLatest(state.config.initialLoadSize)
                LoadType.REFRESH -> {
                    val max = postRemoteKeyDao.max()
                    if (max != null) {
                        apiService.getAfter(max, state.config.pageSize)
                    } else {
                        apiService.getLatest(state.config.initialLoadSize)
                    }
                }
                //элементы сверху
                LoadType.PREPEND -> {
                    Log.d("MyAppLog", "PostRemoteMediator * LoadType.PREPEND:")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                //элементы снизу
                LoadType.APPEND -> {
                    Log.d("MyAppLog", "PostRemoteMediator * LoadType.APPEND: ${state.config.pageSize}")
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                Log.e("MyAppLog", "PostRemoteMediator * ERROR: ${response.code()} / ${response.message()}")
                throw ApiError(response.code(), response.message())
            }

            //val body = response.body() ?: throw HttpException(response) ??
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            appDb.withTransaction {
                //зпись ключей в БД с использование транзакции
                when (loadType) {
                    LoadType.REFRESH -> {
                        //postRemoteKeyDao.removeAll()
                        Log.d("MyAppLog", "PostRemoteMediator * LoadType.REFRESH (DB): ${body.first().id} / ${body.last().id}")
                        if (postRemoteKeyDao.isEmpty()) {
                            Log.d("MyAppLog", "PostRemoteMediator * KeyType.AFTER / BEFORE (DB): ${body.first().id} / ${body.last().id}")
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.BEFORE,
                                        id = body.last().id,
                                    ),
                                )
                            )
                        } else {
                                Log.d("MyAppLog", "PostRemoteMediator * KeyType.AFTER (DB): ${body.first().id} / ${body.last().id}")
                                postRemoteKeyDao.insert(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    )
                                )
                        }
                    }
                    /* не имеет смысла так как выше блок PREPEND - return MediatorResult.Success(endOfPaginationReached = true)
                    //элементы сверху
                    LoadType.PREPEND -> {
                        Log.d("MyAppLog", "PostRemoteMediator * LoadType.PREPEND (DB): ${body.first().id} / ${body.last().id}")
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id,
                            )
                        )
                    }
                    */
                    //элементы снизу
                    LoadType.APPEND -> {
                        Log.d("MyAppLog", "PostRemoteMediator * LoadType.APPEND (DB): ${body.first().id} / ${body.last().id}")
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                    else -> Unit
                }
                postDao.insert(body.toEntity())
                //postDao.insert(body.map(PostEntity::fromDto)) ?? при такой конструкции выделяются отдельные иконки поста
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            Log.e("MyAppLog", "PostRemoteMediator * ERROR: $e")
            return MediatorResult.Error(e)
        }
    }

}
