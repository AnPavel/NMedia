package ru.netology.nmedia

import android.app.Application
import ru.netology.nmedia.auth.AppAuth

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        AppAuth.initApp(this)
    }
}
