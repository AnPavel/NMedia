package ru.netology.nmedia.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.DateSeparator
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.utils.SingleLiveEvent
import ru.netology.nmedia.BuildConfig
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random


//пустой пост
private val empty = Post(
    id = 0L,
    authorId = 0L,
    authorAvatar = "",
    author = "",
    content = "",
    published = LocalDateTime.now(),
    likedByMe = false,
    likes = 0,
    countShare = 0,
    countRedEye = 0,
    linkToVideo = "",
    hiddenEntry = false,
    //ownedByMe = false,
)

private val today = LocalDateTime.now()
private val yesterday = today.minusDays(1)
private val weekAgo = today.minusWeeks(2)


fun Post?.isToday(): Boolean {
    Log.d("MyAppLog", "PostViewModel * isToday: TODAY = $today / $this")
    if (this == null) return false

    return published > yesterday
}

fun Post?.isYesterday(): Boolean {
    Log.d("MyAppLog", "PostViewModel * isYesterday: Published = ${this?.published?.dayOfYear} / Today = ${today.dayOfYear} / $this")
    if (this == null) return false

    return today.year == published.year && published.dayOfYear == yesterday.dayOfYear
}

fun Post?.isWeekAgo(): Boolean {
    Log.d("MyAppLog", "PostViewModel * isWeekAgo: Published = ${this?.published?.dayOfYear} / Today = ${today.dayOfYear} / $this")
    if (this == null) return false

    return published < weekAgo
}


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


    private val cached: Flow<PagingData<FeedItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                terminalSeparatorType = TerminalSeparatorType.SOURCE_COMPLETE,
                generator = { before, after ->

                    when {
                        before == null && after.isToday() -> {
                            DateSeparator(DateSeparator.Type.TODAY)
                            Log.d("MyAppLog", "PostViewModel * insertDateSeparators 1: ${after.isToday()}")
                        }

                        (before == null && after.isYesterday()) || (before.isToday() && after.isYesterday()) -> {
                            DateSeparator(DateSeparator.Type.YESTERDAY)
                            Log.d("MyAppLog", "PostViewModel * insertDateSeparators 2: ${after.isYesterday()}")
                        }

                        before.isYesterday() && after.isWeekAgo() -> {
                            DateSeparator(DateSeparator.Type.WEEK_AGO)
                            Log.d("MyAppLog", "PostViewModel * insertDateSeparators 3: ${after.isWeekAgo()}")
                        }

                        else -> {
                            Log.d("MyAppLog", "PostViewModel * insertDateSeparators 4: ")
                            DateSeparator(DateSeparator.Type.WEEK_AGO)
                        }

                    }

                    if (before?.id?.rem(5) != 0L) null else
                        Ad(
                            Random.nextLong(),
                            "https://netology.ru",
                            "figma.jpg"
                        )

                }
            )
        }
        .cachedIn(viewModelScope)


    private fun insertDateSeparators(before: Post?, after: Post?): DateSeparator? {
        Log.d("MyAppLog", "PostViewModel * insertDateSeparators: $before / $after")
        return when {
            before == null && after.isToday() -> {
                DateSeparator(DateSeparator.Type.TODAY)
            }

            (before == null && after.isYesterday()) || (before.isToday() && after.isYesterday()) -> {
                DateSeparator(DateSeparator.Type.YESTERDAY)
            }

            before.isYesterday() && after.isWeekAgo() -> {
                DateSeparator(DateSeparator.Type.WEEK_AGO)
            }

            else -> {
                null
            }
        }
    }


    //список постов
    // data - только для чтения, посты не изменяются
    val data: Flow<PagingData<FeedItem>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { item ->
                    if (item !is Post) item else item.copy(ownedByMe = item.authorId == myId)
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
        Log.d(
            "MyAppLog",
            "PostViewModel * getAttachmentUrl: $${BuildConfig.BASE_URL}media/${edited.value?.attachment?.url}"
        )
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
