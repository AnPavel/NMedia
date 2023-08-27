package ru.netology.nmedia.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import ru.netology.nmedia.BuildConfig
import javax.inject.Inject

//пустой пост
private val empty = Post(
    id = 0L,
    authorId = 0L,
    authorAvatar = "",
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    countShare = 0,
    countRedEye = 0,
    linkToVideo = "",
    hiddenEntry = false,
    //ownedByMe = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {

    //private val repository: PostRepository = PostRepositoryImpl(AppDb.getInstance(application).postDao())
    //private val repository: PostRepository = PostRepositorySQLiteImpl(AppDb.getInstance(application).postDao)
    //private val repository: PostRepository = PostRepositoryInMemoryImplementation()
    //private val repository: PostRepository = PostRepositoryFileImpl(application)
    //private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)

    //private val _data = MutableLiveData(FeedModel())

    private val cached = repository
        .data
        .cachedIn(viewModelScope)
    //список постов
    // data - только для чтения, посты не изменяются
    val data: Flow<PagingData<Post>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    /*
    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }
        .distinctUntilChanged()

     */

    //текущий пост
    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>()
    val photo: LiveData<PhotoModel?>
        get() = _photo


    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            // Начинаем загрузку
            _dataState.value = FeedModelState(loading = true)
            //repository.getAll()  //при использовании page3 не загружаем все
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "load")
        }
    }

    fun getAttachmentUrl(): String? {
        Log.d("MyAppLog", "PostViewModel * getAttachmentUrl: $${BuildConfig.BASE_URL}media/${edited.value?.attachment?.url}")
        return if (edited.value?.attachment?.url != null)
            "${BuildConfig.BASE_URL}media/${edited.value?.attachment?.url}"
        else null
    }

    fun showHiddenPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.showAll()
            _dataState.value = FeedModelState()
        } catch (e: java.lang.Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "load")
        }
    }

    fun refresh() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            //repository.getAll()  //при использовании page3 не загружаем все
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "refresh")
        }
    }

    fun removeEdit() {
        edited.value = empty
    }

    fun save() = viewModelScope.launch {
        try {
            edited.value?.let {
                Log.d("MyAppLog", "PostViewModel * save: $it")
                when (val photo = _photo.value) {
                    null -> repository.save(it.copy(ownedByMe = true))
                    else -> repository.saveWithAttachment(it.copy(ownedByMe = true), photo)
                }
            }
            Log.d("MyAppLog", "PostViewModel * save_2: $_postCreated")
            edited.value = empty
            _postCreated.postValue(Unit)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "save")
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content !== text) {
            edited.value = edited.value?.copy(content = text)
        }
        Log.d("MyAppLog", "PostViewModel * changeContent: ${this.edited.value?.content}")
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
            _dataState.value = FeedModelState(loading = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "like")
        }
    }

    fun shareById(id: Long) = viewModelScope.launch {
        try {
            repository.shareById(id)
            _dataState.value = FeedModelState(loading = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "share")
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState(loading = true)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true, errStateCodeTxt = "remove")
            }
        }
    }

    fun setPhoto(photoModel: PhotoModel) {
        _photo.value = photoModel
    }

    fun clearPhoto() {
        _photo.value = null
    }

}
