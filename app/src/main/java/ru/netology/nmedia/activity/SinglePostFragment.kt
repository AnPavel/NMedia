package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class SinglePostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        //val fragment2 = SinglePostFragment.newInstance(1)
        val arg2 = arguments?.textArg?.toInt()
        val viewHolder = PostViewHolder(binding.post, object : OnInteractionListener {
            /* переопределить методы */

        })
        //val viewModel: PostViewModel by activityViewModels()
        //viewHolder.bind(viewModel.data.value.orEmpty().get(arg2!!))
        viewHolder.bind(viewModel.data.value.orEmpty().get(arg2!!))

        return binding.root
    }

    companion object {
        //var Bundle.idPostArg: Long by IntArg
        var Bundle.textArg: String? by StringArg
/*
        @JvmStatic
        private val ARG_IDPOST = "ARG_IDPOST"

        @JvmStatic
        fun newInstance(idPost: Long): SinglePostFragment {
            val args = Bundle().apply {
                putLong(ARG_IDPOST, idPost)
            }
            val fragment = SinglePostFragment()
            fragment.arguments = args
            return fragment
        }
 */
    }

}
