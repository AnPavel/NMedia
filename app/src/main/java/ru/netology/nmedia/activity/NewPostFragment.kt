package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.utils.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    //private val viewModel: PostViewModel by activityViewModels()
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    private val photoPickerContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("MyAppLog","NewPostFragment * photoPickerContract")
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> Toast.makeText(
                    requireContext(),
                    R.string.picker_error,
                    Toast.LENGTH_SHORT
                ).show()
                Activity.RESULT_OK -> {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    viewModel.setPhoto(PhotoModel(uri, uri.toFile()))
                }
            }
        }

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


        binding.gallery.setOnClickListener {
            Log.d("MyAppLog","NewPostFragment * gallery")
            ImagePicker.with(this)
                .compress(2048)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .crop()
                .createIntent(photoPickerContract::launch)
        }

        binding.takePhoto.setOnClickListener {
            Log.d("MyAppLog","NewPostFragment * takePhoto")
            ImagePicker.with(this)
                .cameraOnly()
                .crop()
                .createIntent(photoPickerContract::launch)
        }


        binding.clear.setOnClickListener {
            Log.d("MyAppLog","NewPostFragment * clear")
            viewModel.clearPhoto()
        }

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            Log.d("MyAppLog","NewPostFragment * photo: $photo")
            if (photo == null) {
                binding.previewPhotoContainer.isGone = true
                binding.previewPhoto.setImageURI(null)
                return@observe
            }
            binding.previewPhotoContainer.isVisible = true
            binding.previewPhoto.setImageURI(photo.uri)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.new_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        val text = binding.edit.text.toString()
                        if (text.isNotBlank()) {
                            viewModel.changeContent(text)
                            viewModel.save()
                        } else {
                            Toast.makeText(
                                this@NewPostFragment.requireContext(),
                                R.string.empty_content_warning,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner) //меню убрать из экшенбара

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
