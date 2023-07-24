package ru.netology.nmedia.listener

import android.content.Context
import android.content.Intent
import android.net.Uri
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


open class OnInteractionListenerImpl(
    private val context: Context,
    private val viewModel: PostViewModel
) : OnInteractionListener {

    override fun onLike(post: Post) {
        viewModel.likeById(post.id)
    }

    override fun onShare(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }

        val shareIntent =
            Intent.createChooser(intent, context.getString(R.string.chooser_share_post))
        context.startActivity(shareIntent)
        viewModel.shareById(post.id)
    }

    override fun onPlayVideo(post: Post) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.linkToVideo))
        val playIntent =
            Intent.createChooser(intent, context.getString(R.string.chooser_share_post))
        context.startActivity(playIntent)
    }


    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
    }

    override fun onEdit(post: Post) {
        viewModel.edit(post)
    }
}
