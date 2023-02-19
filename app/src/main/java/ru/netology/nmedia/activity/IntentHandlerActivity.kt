package ru.netology.nmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_intent_handler)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let /* выход из лямбда функции через передачу @let */
            }
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            /*проверить тект на пусто*/
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
            }
        }
    }
}
