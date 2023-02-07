package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the message from the intent
        //val message = intent?.getStringExtra(EXTRA_MESSAGE)
        val message = intent?.getStringExtra(Intent.EXTRA_TEXT)

        //Log.d("MyLog","intent1=" + intent.getStringExtra("EXTRA_TEXT"))
        //Log.d("MyLog","intent1=" + intent?.getStringExtra(Intent.EXTRA_TEXT))
        Log.d("MyLog","intent1=" + intent)
        Log.d("MyLog","intent1=" + message)


        /* добавить string в поле на экране */
        //binding.content?.setText(message)
        binding.content?.setText("This is my text to post.")

        /* обработка события по контракту если пусто - вернуть Cancel, иначе OK */
        binding.buttonOk.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_TEXT, text) })
            }
            /* скрыть активити */
            finish()
        }
    }

    object NewPostContract : ActivityResultContract<Unit, String?>() {

        override fun createIntent(context: Context, input: Unit) =
            Intent(context, NewPostActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?) =
            intent?.getStringExtra(Intent.EXTRA_TEXT)

    }
}
