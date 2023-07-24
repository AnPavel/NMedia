package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent

//пустой пост
private val empty = Post(
    id = 0,
    authorAvatar = "",
    author = "",
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    countShare = 0,
    countRedEye = 0,
    linkToVideo = "",
    hiddenEntry = false
)


class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao())
    //private val repository: PostRepository = PostRepositorySQLiteImpl(AppDb.getInstance(application).postDao)
    //private val repository: PostRepository = PostRepositoryInMemoryImplementation()
    //private val repository: PostRepository = PostRepositoryFileImpl(application)
    //private val repository: PostRepository = PostRepositorySharedPrefsImpl(application)

    //private val _data = MutableLiveData(FeedModel())
    //список постов
    // data - только для чтения, посты не изменяются
    val data: LiveData<FeedModel> = repository.data
        .map(::FeedModel)
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }
        .distinctUntilChanged()

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
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "load")
        }
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
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errStateCodeTxt = "refresh")
        }
    }

    fun save() = viewModelScope.launch {
        try {
            edited.value?.let {
                when (val photo = _photo.value) {
                    null -> repository.save(it)
                    else -> repository.saveWithAttachment(it, photo)
                }

            }
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
