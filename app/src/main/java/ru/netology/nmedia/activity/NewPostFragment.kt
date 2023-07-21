package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.internal.ViewUtils.hideKeyboard
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    //private val viewModel: PostViewModel by activityViewModels()
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        arguments?.textArg
            ?.let(binding.edit::setText)
        binding.edit.requestFocus()

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                        //AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner) //меню убрать из экшенбара

        /*
        binding.ok.setOnClickListener {
            val text = binding.edit.text.toString()
            //проверка на заполнение, не пустое поле
            if (text.isNotBlank()) {
                viewModel.changeContent(text)
                viewModel.save()
            } else {
                Toast.makeText(
                    this.context,
                    R.string.empty_content_warning,
                    Toast.LENGTH_SHORT
                ).show()
            }
            //AndroidUtils.hideKeyboard(requireView())
            //findNavController().navigateUp()

        }
        */

        /* подписываемся */
        viewModel.postCreated.observe(viewLifecycleOwner)
        {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        /**/
        return binding.root
    }
}
