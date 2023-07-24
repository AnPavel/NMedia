package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.listener.OnInteractionListener
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onEdit(post: Post) {
                Log.d("MyAppLog","FeedFragment * adapter onEdit: $post")
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                    }
                )
            }

            override fun onOpenPost(post: Post) {
                Log.d("MyAppLog","FeedFragment * adapter onOpenPost: $post")
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }

            override fun onShowAttachment(post: Post) {
                Log.d("MyAppLog","FeedFragment * adapter onShowAttachment: $post")
                findNavController().navigate(
                    R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply {
                        textArg = "${BuildConfig.BASE_URL}media/${post.attachment!!.url}"
                    })
            }

            override fun onLike(post: Post) {
                Log.d("MyAppLog","FeedFragment * adapter onLike: $post")
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                Log.d("MyAppLog","FeedFragment * adapter onRemove: $post")
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

        })
        binding.list.adapter = adapter
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            Log.d("MyAppLog","FeedFragment * data_state: $state")
            binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                if (state.errStateCodeTxt == "load") {
                    Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.buttom_snackbar_txt) {
                            viewModel.loadPosts()
                        }
                        .show()
                }
                if (state.errStateCodeTxt == "refresh") {
                    Snackbar.make(binding.root, R.string.error_refresh, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.buttom_snackbar_txt) {
                            viewModel.refresh()
                        }
                        .show()
                }
                if (state.errStateCodeTxt == "save") {
                    Snackbar.make(binding.root, R.string.error_save, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.buttom_snackbar_txt) {
                            viewModel.save()
                        }
                        .show()
                }
                if (state.errStateCodeTxt == "like") {
                    Snackbar.make(binding.root, R.string.error_like, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.buttom_snackbar_txt) {
                            viewModel.refresh()
                        }
                        .show()
                }
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            Log.d("MyAppLog","FeedFragment * data: $state")
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.swipeRefresh.setOnRefreshListener {
            Log.d("MyAppLog","FeedFragment * swipeRefresh - обновление экрана")
            binding.swipeRefresh.isRefreshing = true
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            Log.d("MyAppLog","FeedFragment * newer count - новых записей: $it")
            if (it > 0) {
                binding.newPostsButton.visibility = VISIBLE
            }
        }
        binding.newPostsButton.setOnClickListener {
            Log.d("MyAppLog","FeedFragment * showHiddenPosts - отображение кнопки")
            viewModel.showHiddenPosts()
            binding.list.smoothScrollToPosition(0)
            binding.newPostsButton.visibility = INVISIBLE
        }

        return binding.root
    }
}
