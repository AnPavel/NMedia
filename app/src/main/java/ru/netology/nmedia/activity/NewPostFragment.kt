package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(layoutInflater, container, false)
        //val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
        //arguments?.textArg?.let {
        //    binding.edit.settext(it)
        //

        arguments?.textArg?.let(binding.content::setText)
        binding.content.requestFocus()

        binding.buttonOk.setOnClickListener {
            viewModel.changeContent(binding.content.text.toString())
            viewModel.save()
            //AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        return binding.root

    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }

}
