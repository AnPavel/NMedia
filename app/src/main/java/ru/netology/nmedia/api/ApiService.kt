package ru.netology.nmedia.api
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.*


interface ApiService {

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
    ): Response<User>  //Response<Token>

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

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body body: PushToken): Response<Unit>

}
