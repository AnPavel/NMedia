package ru.netology.nmedia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.listener.OnInteractionListenerImpl
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.OfferToAuthenticate
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(
            inflater,
            container,
            false
        )

        //val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val viewModel: PostViewModel by viewModels()

        //val authViewModel: AuthViewModel by viewModels(ownerProducer = ::requireParentFragment)
        val authViewModel: AuthViewModel by viewModels()

        val currentPostId = requireArguments().textArg?.toLong()

        val interactionListener by lazy {
            object : OnInteractionListenerImpl(this.requireActivity(), viewModel) {

                override fun onEdit(post: Post) {
                    Log.d("MyAppLog", "PostFragment * onEdit: $post")
                    super.onEdit(post)
                    findNavController().navigate(
                        R.id.action_postFragment_to_newPostFragment,
                        Bundle().apply {
                            textArg = post.content
                        })
                }

                override fun onRemove(post: Post) {
                    Log.d("MyAppLog", "PostFragment * onRemove: $post")
                    super.onRemove(post)
                    findNavController().navigate(R.id.action_postFragment_to_feedFragment)
                }

                override fun onShowAttachment(post: Post) {
                    Log.d("MyAppLog", "PostFragment * onShowAttachment: $post")
                    findNavController().navigate(
                        R.id.action_postFragment_to_photoFragment,
                        Bundle().apply {
                            textArg = "${BuildConfig.BASE_URL}media/${post.attachment!!.url}"

                        })
                }

                override fun onLike(post: Post) {
                    Log.d("MyAppLog", "PostFragment * onLike: $post")
                    if (authViewModel.isAuthorized) {
                        super.onLike(post)
                    } else {
                        OfferToAuthenticate.remind(
                            binding.root,
                            "You should sign in to like posts!",
                            this@PostFragment
                        )
                    }
                }

                override fun onShare(post: Post) {
                    Log.d("MyAppLog", "PostFragment * onShare: $post")
                    if (authViewModel.isAuthorized) {
                        super.onShare(post)
                    } else {
                        OfferToAuthenticate.remind(
                            binding.root,
                            "You should sign in to share posts!",
                            this@PostFragment
                        )
                    }
                }

            }
        }

        /* TODO Architecture Components 1
        binding.post.apply {
            viewModel.data.observe(viewLifecycleOwner) { it ->
                val viewHolder = PostViewHolder(binding.post, interactionListener)
                val post = it.posts.find { it.id == currentPostId }
                post?.let { viewHolder.bind(post) }
            }
        }

         */

        return binding.root
    }
}
