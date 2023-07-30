package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.User

//private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
private const val BASE_URL = "${BuildConfig.BASE_URL}api/slow/"

//логирование (подключен в gradle)
private val logging = HttpLoggingInterceptor().apply {
    //смотрим какой тип сборки
    if (BuildConfig.DEBUG) {
        //устанавливаем уровень - максимум
        level = HttpLoggingInterceptor.Level.BODY
    } else {
        //устанавливаем уровень - без логирования
        HttpLoggingInterceptor.Level.NONE
    }
}

//создаем объект OkHttpClient
private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
        AppAuth.getInstance().authStateFlow.value.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
        chain.proceed(chain.request())
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostApiService {

    //запрос на список постов - ответ список постов
    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    //запрос на пост по id - ответ пост
    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    //запрос на сохранение - ответ один сохраненный пост
    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    //запрос на удаление конкретного поста
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    //запрос на удаление лайка
    @DELETE("posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<Post>

    //запрос на установку лайка
    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun uploadMedia(@Part media: MultipartBody.Part): Response<Media>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<User>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<User>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<User>

    @POST("users/push-token")
    suspend fun sendPushToken(@Body body: PushToken): Response<Unit>

}

object PostApi {
    val service: PostApiService by lazy {
        //retrofit.create(PostApiService::class.java)
        retrofit.create()
    }
}
