package ru.netology.nmedia.auth

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.PushToken

class AppAuth private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getLong(idKey, 0)
        val token = prefs.getString(tokenKey, null)

        if (id == 0L || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
        sendPushToken()
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            val tokenDto = PushToken(token ?: Firebase.messaging.token.await())
            Log.d("MyAppLog", "AppAuth * sendPushToken: $tokenDto")

            runCatching {
                PostApi.service.sendPushToken(tokenDto)
            }

        }
    }

    companion object {
        @Volatile
        private var instance: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            instance ?: throw IllegalStateException(
                "AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initApp(context: Context): AppAuth = instance ?: synchronized(this) {
            instance ?: buildAuth(context).also { instance = it }
        }

        private fun buildAuth(context: Context): AppAuth = AppAuth(context)
    }
}

data class AuthState(val id: Long = 0, val token: String? = null)


/*
class AppAuth private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val _data = MutableStateFlow<Token?>(null)
    val data = _data.asStateFlow()

    init{
        //считываем
        val token = prefs.getString(TOKEN_KEY, null)
        val id = prefs.getLong(ID_KEY, 0L)
        //проверка на отсутствие
        if (token == null || id == 0L){
            prefs.edit { clear() }
        } else {
            //записываем информацию
            _data.value = Token(id, token)
        }
    }

    companion object{

        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"

        @Volatile
        private var INSTANCE: AppAuth? = null

        fun initApp(context: Context){
            INSTANCE = AppAuth(context)
        }

        fun getInstance(): AppAuth = requireNotNull(INSTANCE) {
            "You must call to police before (I'm jokin, I mean initApp)"
        }

    }

    @Synchronized
    fun setToken(token: Token){
        _data.value = token
        prefs.edit{
            putString(TOKEN_KEY, token.token)
            putLong(ID_KEY, token.id)

        }

    }

    @Synchronized
    fun clearAuth(){
        _data.value = null
        prefs.edit { clear() }
    }

}

 */
