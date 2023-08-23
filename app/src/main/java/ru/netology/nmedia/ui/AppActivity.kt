package ru.netology.nmedia.ui

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
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity(R.layout.activity_app) {

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    @Inject
    lateinit var repository: PostRepository

    @Inject
    lateinit var messageService: FirebaseMessaging

    @Inject
    lateinit var messageServiceInit: FirebaseInstallations

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

        messageServiceInit.id.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println(" - some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MyAppLog", "AppActivity * FirebaseInstallations: $token")
            println(token)
        }

        messageService.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println(" - some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MyAppLog", "AppActivity * FirebaseMessaging: $token")
            println(token)
        }

        //проверка на доступность Google сервисов
        checkGoogleApiAvailability()
        //разрешение на выполнение уведомлений
        requestNotificationsPermission()

        val nomVersionApp = System.getProperty("os.version")
        Log.d("MyAppLog", "AppActivity * OS: $nomVersionApp")

        var oldMenuProvider: MenuProvider? = null
        viewModel.data.observe(this) {
            oldMenuProvider?.let {
                removeMenuProvider(it)
            }

            //отображение меню в зависимости от авторизован пользователь или нет
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)

                    menu.let {
                        it.setGroupVisible(R.id.unauthorized, !viewModel.isAuthorized)
                        it.setGroupVisible(R.id.authorized, viewModel.isAuthorized)
                    }

                    /* старый вариант больше кода
                    val authorized = authViewModel.isAuthorized
                    if(authorized){
                        menu.setGroupVisible(R.id.authorized, true)
                        menu.setGroupVisible(R.id.unauthorized, false)
                    }else{
                        menu.setGroupVisible(R.id.authorized, false)
                        menu.setGroupVisible(R.id.unauthorized, true)
                    }
                    */
                }

                override fun onMenuItemSelected(item: MenuItem): Boolean =
                    when (item.itemId) {
                        R.id.sign_in -> {
                            Log.d(
                                "MyAppLog",
                                "AppActivity * menu sign_in - Авторизация пользователя через логин и пароль"
                            )
                            findNavController(R.id.activity_app_layout).navigate(R.id.signInFragment)
                            true
                        }

                        R.id.sign_up -> {
                            Log.d(
                                "MyAppLog",
                                "AppActivity * menu sign_up - Ввод данных нового пользователя"
                            )
                            findNavController(R.id.activity_app_layout).navigate(R.id.userRegNewFragment)
                            true
                        }

                        R.id.logout -> {
                            Log.d("MyAppLog", "AppActivity * menu logout")
                            findNavController(R.id.activity_app_layout).navigate(R.id.logoutDialog)
                            //appAuth.removeAuth()
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
        //with(GoogleApiAvailability.getInstance()) {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                Log.d("MyAppLog", "AppActivity * checkGoogleApiAvailability (success) code: 0")
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Log.d("MyAppLog", "AppActivity * checkGoogleApiAvailability (!success) code: $code")
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d(
                "MyAppLog",
                "AppActiviity * version_SDK_INT: ${Build.VERSION.SDK_INT} (notifications not allowed)"
            )
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(
                "MyAppLog",
                "AppActiviity * requestNotificationsPermission: ${checkSelfPermission(permission)}"
            )
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}
