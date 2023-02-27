package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class SinglePostFragment : Fragment() {

    //private val viewModel: PostViewModel by viewModels(
    //    ownerProducer = ::requireParentFragment
    //)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

        val viewHolder = PostViewHolder(binding.post, object : OnInteractionListener {
            /* переопределить методы */

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
}
