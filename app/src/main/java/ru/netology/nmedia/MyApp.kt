package ru.netology.nmedia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.nmedia.auth.AppAuth

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        //AppAuth.initApp(this)
    }

}
