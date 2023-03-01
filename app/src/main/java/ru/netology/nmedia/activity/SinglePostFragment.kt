package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class SinglePostFragment : Fragment() {

    //private val viewModel: PostViewModel by viewModels(
    //    ownerProducer = ::requireParentFragment
    //)
    private var _binding: FragmentPostBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        val viewHolder = PostViewHolder(binding.post, object : OnInteractionListener {
            /* переопределить методы */
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    //putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.likeByShareId(post.id)
            }

            override fun onRedEye(post: Post) {
                viewModel.likeByRedEyeId(post.id)
            }


        })

        val currentPostId = requireArguments().textArg!!.toLong()

        binding.post.apply {
            viewModel.data.observe(viewLifecycleOwner) { it ->
                //val viewHolder = PostViewHolder(binding.post, object : OnInteractionListener)
                val post = it.find { it.id == currentPostId }
                post?.let { viewHolder.bind(post) }
            }
        }
        return binding.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
