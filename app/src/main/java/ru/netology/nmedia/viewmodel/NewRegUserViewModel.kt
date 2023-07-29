package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.AuthModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.AuthRepositoryImpl
import java.io.File

class NewRegUserViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()

    private val _data = MutableLiveData<User>()
    val data: LiveData<User>
        get() = _data

    private val _dataState = MutableLiveData<AuthModelState>()
    val dataState: LiveData<AuthModelState>
        get() = _dataState

    private val _avatar = MutableLiveData<PhotoModel?>(null)
    val avatar: LiveData<PhotoModel?>
        get() = _avatar


    fun registerUser(login: String, pass: String, name: String) {
        viewModelScope.launch {
            try {
                val user = repository.registerUser(login, pass, name)
                _data.value = user
            } catch (e: Exception) {
                _dataState.postValue(AuthModelState(error = true))
            }
        }
    }


    fun registerUserWithPhoto(login: String, pass: String, name: String, upload: MediaUpload) {
        viewModelScope.launch {
            try {
                _avatar.value?.file?.let { file ->
                    val user = repository.registerUserWithPhoto(
                        login = login,
                        pass = pass,
                        name = name,
                        upload = MediaUpload(file)
                    )
                    _data.value = user
                }

            } catch (e: Exception) {
                _dataState.postValue(AuthModelState(error = true))
            }
        }
    }

    fun clearPhoto() {
        _avatar.value = null
    }

    fun changeAvatar(uri: Uri?, file: File?) {
        _avatar.value = PhotoModel(uri, file)
    }

    fun clean(){
        _dataState.value = AuthModelState(loading = false, error = false, success = false)
    }

}
