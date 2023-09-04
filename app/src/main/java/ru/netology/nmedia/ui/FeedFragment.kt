package ru.netology.nmedia.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.SocketTimeoutError
import ru.netology.nmedia.utils.OfferToAuthenticate
import ru.netology.nmedia.listener.OnInteractionListener
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment() {

    @Inject
    lateinit var repository: PostRepository

    @Inject
    lateinit var auth: AppAuth

    //val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    val viewModel: PostViewModel by viewModels()

    //val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)
    val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        val adapter = PostsAdapter(object : OnInteractionListener {

            override fun onOpenPost(post: Post) {
                Log.d("MyAppLog", "FeedFragment * adapter onOpenPost: $post")
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }

            override fun onEdit(post: Post) {
                Log.d("MyAppLog", "FeedFragment * adapter onEdit: $post")
                viewModel.getAttachmentUrl()
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = post.content
                        //textArg = post.id.toString()
                    }
                )
            }

            override fun onRemove(post: Post) {
                Log.d("MyAppLog", "FeedFragment * adapter onRemove: $post")
                viewModel.removeById(post.id)
            }


            override fun onShowAttachment(post: Post) {
                Log.d("MyAppLog", "FeedFragment * adapter onShowAttachment: $post")
                findNavController().navigate(
                    R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply {
                        textArg = "${BuildConfig.BASE_URL}media/${post.attachment!!.url}"
                    })
            }

            override fun onLike(post: Post) {
                Log.d("MyAppLog", "FeedFragment * adapter onLike: $post")
                if (authViewModel.isAuthorized) {
                    viewModel.likeById(post.id)
                    sendUpstream()
                } else {
                    OfferToAuthenticate.remind(
                        binding.root,
                        "You should sign in to like posts!",
                        this@FeedFragment
                    )
                }
            }

            override fun onShare(post: Post) {
                Log.d("MyAppLog", "FeedFragment * adapter onShare: $post")
                if (authViewModel.isAuthorized) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }

                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.snak_auth),
                        BaseTransientBottomBar.LENGTH_SHORT,
                    )
                        .setAction(getString(R.string.confirm)) {
                            findNavController().navigate(R.id.action_feedFragment_to_signInFragment)
                        }
                        .show()
                }
            }

        })
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter { adapter.retry() },
            footer = PostLoadingStateAdapter { adapter.retry() }
        )
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            Log.d("MyAppLog", "FeedFragment * data_state: $state")
            //binding.progress.isVisible = state.loading
            binding.swipeRefresh.isRefreshing = state.refreshing
            if (state.error) {
                if (state.errStateCodeTxt == "load") {
                    Snackbar.make(binding.root,
                        R.string.error_loading,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.buttom_snackbar_txt) {
                            viewModel.loadPosts()
                        }
                        .show()
                }
                if (state.errStateCodeTxt == "refresh") {
                    Snackbar.make(binding.root,
                        R.string.error_refresh,
                        Snackbar.LENGTH_INDEFINITE)
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

        //подписка на flow и отправка данных в adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                // Корутина будет запущена и будет повторяться только при состоянии CREATED
                Log.d("MyAppLog", "FeedFragment * viewModel.data.collectLatest")
                viewModel.data.collectLatest(adapter::submitData)
                //viewModel.data.collectLatest { state ->
                //    adapter.submitData(state)
                //}
            }
        }
        //индикатор загрузки
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { state ->
                Log.d("MyAppLog", "FeedFragment * adapter.loadStateFlow.collectLatest")
                binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
                //добавлено для ошибки при рестарте приложения и авторизации пользователя
                if (state.refresh is LoadState.Error) {
                    val errorState = state.refresh as LoadState.Error
                    Log.d("MyAppLog", "FeedFragment * LoadState.Error1 ${state.refresh}")
                    Log.d("MyAppLog", "FeedFragment * LoadState.Error2 ${errorState.error}")
                    when (errorState.error) {
                        is IOException -> Log.e("MyAppLog", "IOException")
                        is SocketTimeoutError -> Log.e("MyAppLog", "SocketTimeoutError")
                        is ApiError -> {
                            Log.e("MyAppLog", "ApiError")
                            auth.removeAuth() //Убираем авторизацию
                            adapter.refresh()
                        }
                    }
                }
            }
        }

        /*
        // вариант выдает - Job’ is deprecated. launchWhenCreated is deprecated
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { state ->
                adapter.submitData(state)
            }
        }

        //индикатор загрузки
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                Log.d("MyAppLog", "FeedFragment * adapter.loadStateFlow.collectLatest")
                binding.swipeRefresh.isRefreshing =
                    state.refresh is LoadState.Loading ||
                            state.prepend is LoadState.Loading ||
                            state.append is LoadState.Loading
            }
        }
        */


        //загрузка новых и очищение старых
        binding.swipeRefresh.setOnRefreshListener(adapter::refresh)
        //binding.swipeRefresh.setOnRefreshListener {
        //    adapter.refresh()
        //}


        /*
        viewModel.data.observe(viewLifecycleOwner) { date ->
            Log.d("MyAppLog", "FeedFragment * data:")
            //Log.d("MyAppLog", "FeedFragment * data: $date")
            val newPost = adapter.currentList.size < date.posts.size
            adapter.submitList(date.posts) {
                if (newPost) {
                    if (binding.newPostsButton.visibility == INVISIBLE) {
                        binding.list.smoothScrollToPosition(0)
                    }
                }
            }
        }
         */

        authViewModel.data.observe(viewLifecycleOwner) { adapter.refresh() }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.fab.setOnClickListener {
            if (authViewModel.isAuthorized) {
                viewModel.removeEdit()
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            } else {
                OfferToAuthenticate.remind(
                    binding.root,
                    "You should sign in to add/edit posts!",
                    this@FeedFragment
                )
            }
        }

        /*
        binding.swipeRefresh.setOnRefreshListener {
            Log.d("MyAppLog", "FeedFragment * swipeRefresh - обновление экрана")
            binding.swipeRefresh.isRefreshing = true
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.newerCount.observe(viewLifecycleOwner) {
            Log.d("MyAppLog", "FeedFragment * newer count - новых записей: $it")
            if (it > 0) {
                val recentEntries = getString(R.string.recent_entries)
                binding.newPostsButton.text = "$recentEntries: $it"
                binding.newPostsButton.visibility = VISIBLE
            }
        }

        binding.newPostsButton.setOnClickListener {
            Log.d("MyAppLog", "FeedFragment * showHiddenPosts - отображение кнопки")
            viewModel.showHiddenPosts()
            binding.list.smoothScrollToPosition(0)
            binding.newPostsButton.visibility = INVISIBLE
        }
        */

        return binding.root
    }

}

fun sendUpstream() {
    val SENDER_ID = "YOUR_SENDER_ID" //????
    val messageId = 0 // Increment for each
    val fm = Firebase.messaging
    Log.d("MyAppLog",
        "FeedFragment * sendUpstream: sender_id = $SENDER_ID / messageId = $messageId / fm = $fm")
    fm.send(
        remoteMessage("$SENDER_ID@fcm.googleapis.com") {
            setMessageId(messageId.toString())
            addData("my_message", "Hello World")
            addData("my_action", "SAY_HELLO")
        },
    )
}
