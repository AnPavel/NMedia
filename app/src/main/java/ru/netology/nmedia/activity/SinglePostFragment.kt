package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class SinglePostFragment : Fragment() {

    //private val viewModel: PostViewModel by viewModels(
    //    ownerProducer = ::requireParentFragment
    //)
    /*
    companion object {
        fun getNewInstance(args: Bundle?): SinglePostFragment {
            val SinglePostFragment2 = SinglePostFragment()
            SinglePostFragment2.arguments = args
            return SinglePostFragment2
        }

    }
    */

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        val arg2 = arguments?.getLong("idPost")
        val viewHolder = PostViewHolder(binding.post, object : OnInteractionListener {
            /* переопределить методы */

        })
        val viewModel: PostViewModel by activityViewModels()
        //viewHolder.bind(viewModel.data.value.orEmpty().get(idPost))
        viewHolder.bind(viewModel.data.value.orEmpty().get(arg2!!.toInt()))

        return binding.root
    }

}
