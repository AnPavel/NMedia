package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    //private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val newPostContract = registerForActivityResult(NewPostActivity.NewPostContract) { content ->
            content ?: return@registerForActivityResult
            viewModel.changeContent(content)
            viewModel.save()
        }

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                    Log.d("MyLog","intent=" + intent?.getStringExtra(Intent.EXTRA_TEXT))

                //newPostContract.launch()
                newPostContract.launch(viewModel.edit(post))
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    //putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                //Log.d("MyLog","intent=" + intent.putExtra(Intent.EXTRA_TEXT, post.content))
                //Log.d("MyLog","intent=" + intent?.getStringExtra(Intent.EXTRA_TEXT))
                val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            /*********************************/

            override fun onUrl(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkToVideo))
                startActivity(intent)

            }

            /*********************************/

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onRedEye(post: Post) {
                viewModel.likeByRedEyeId(post.id)
            }
        })

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
            /* запускаем контаакт методом lauch */
            newPostContract.launch()
        }

    }
}
