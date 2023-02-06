package ru.netology.nmedia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    val viewModel: PostViewModel by viewModels()

    val interactionListener = object : OnInteractionListener {
        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        /*
        override fun onShare(post: Post) {
            viewModel.likeByShareId(post.id)
        }
        */

        override fun onShare(post: Post) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                //putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
            //val shareIntent = Intent.createChooser(intent, getString(R.string.post_text))
            startActivity(shareIntent)
        }

        override fun onRedEye(post: Post) {
            viewModel.likeByRedEyeId(post.id)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val newPostContract = registerForActivityResult(NewPostActivity.NewPostContract) { content ->
            content ?: return@registerForActivityResult
            viewModel.changeContent(content)
            viewModel.save()
        }

        val adapter = PostsAdapter(interactionListener)
        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts) {
                /* при добавлени нового поста переход на добавленный пост в начало страницы */
                if (newPost) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }
        binding.add.setOnClickListener {
            newPostContract.launch()
        }


        /*
        //binding.content.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        binding.cancel.setOnClickListener {
            viewModel.cancel()
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                with(binding.content) {
                    requestFocus()
                    setText(post.content)
                    AndroidUtils().showKeyboard(this)
                }
            } else {
                with(binding.content) {
                    setText("")
                    clearFocus()
                    AndroidUtils().hideKeyboard(this)
                }
            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    viewModel.changeContent(text.toString())
                    viewModel.save()

                    setText("")
                    clearFocus()
                    AndroidUtils().hideKeyboard(this)
                }
            }
        }
        */

    }
}
