package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.activity_app_layout)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        checkGoogleApiAvailability()
        //разрешение на выполнение уведомлений
        requestNotificationsPermission()

        val nomVersionApp = System.getProperty("os.version")
        Log.d("MyAppLog","AppActivity * OS: $nomVersionApp")

        val authViewModel: AuthViewModel by viewModels()

        var oldMenuProvider: MenuProvider? = null
        authViewModel.data.observe(this) {
            oldMenuProvider?.let {
                removeMenuProvider(it)
            }

            //отображение меню в зависимости от авторизован пользователь или нет
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)
                    val authorized = authViewModel.isAuthorized
                    if(authorized){
                        menu.setGroupVisible(R.id.authorized, true)
                        menu.setGroupVisible(R.id.unauthorized, false)
                    }else{
                        menu.setGroupVisible(R.id.authorized, false)
                        menu.setGroupVisible(R.id.unauthorized, true)
                    }
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean =
                    when (item.itemId) {
                        R.id.sign_in -> {
                            Log.d("MyAppLog","AppActivity * menu sign_in - Авторизация пользователя через логин и пароль")
                            findNavController(R.id.activity_app_layout).navigate(R.id.signInFragment)
                            true
                        }

                        R.id.sign_up -> {
                            Log.d("MyAppLog","AppActivity * menu sign_up - Ввод данных нового пользователя")
                            findNavController(R.id.activity_app_layout).navigate(R.id.userRegNewFragment)
                            true
                        }

                        R.id.logout -> {
                            Log.d("MyAppLog","AppActivity * menu logout")
                            findNavController(R.id.activity_app_layout).navigate(R.id.logoutDialog)
                            true
                        }

                        else -> false

                    }
            }.apply {
                oldMenuProvider = this
            }, this)
        }

    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                Log.d("MyAppLog", "AppActiviity * checkGoogleApiAvailability (success) code: 0")
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Log.d("MyAppLog", "AppActiviity * checkGoogleApiAvailability (!success) code: $code")
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d("MyAppLog", "AppActiviity * version_SDK_INT: ${Build.VERSION.SDK_INT} (notifications not allowed)")
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyAppLog", "AppActiviity * requestNotificationsPermission: ${checkSelfPermission(permission)}")
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}
