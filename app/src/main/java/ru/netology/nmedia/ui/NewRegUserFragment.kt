package ru.netology.nmedia.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.internal.ViewUtils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentUserRegNewBinding
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.utils.*
import ru.netology.nmedia.extens.load
import ru.netology.nmedia.viewmodel.*

@AndroidEntryPoint
class NewRegUserFragment : Fragment() {

    //private val viewModel: NewRegUserViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val viewModel: NewRegUserViewModel by viewModels()

    //private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val postViewModel: PostViewModel by viewModels()

    private val avatarLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("MyAppLog", "NewRegUserFragment * changeAvatar - avatarLauncher")
            val uri: Uri? = it.data?.data
            viewModel.changeAvatar(uri, uri?.toFile())

        } else {
            Toast.makeText(
                requireContext(),
                R.string.picker_error,
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUserRegNewBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.loading.isVisible = state.loading
            binding.signIn.isVisible = !state.loading
            binding.returnOut.isVisible = !state.loading
            binding.incorrect.isVisible = state.error
            binding.avatarsGroup.isVisible = true
            binding.loginField.requestFocus()

            if (state.success) {
                Log.d("MyAppLog", "NewRegUserFragment * state - success")
                postViewModel.refresh()
                viewModel.clean()
                findNavController().navigateUp()
            }
        }

        binding.takePhoto.setOnClickListener {
            Log.d("MyAppLog", "NewRegUserFragment * takePhoto")
            ImagePicker.with(requireActivity())
                .cameraOnly()
                .crop()
                .compress(2048)
                .createIntent(avatarLauncher::launch)
        }

        binding.chooseFromGallery.setOnClickListener {
            Log.d("MyAppLog", "NewRegUserFragment * chooseFromGallery")
            ImagePicker.with(requireActivity())
                .galleryOnly()
                .crop()
                .compress(2048)
                .createIntent(avatarLauncher::launch)
        }

        binding.clearPhoto.setOnClickListener {
            Log.d("MyAppLog", "NewRegUserFragment * clearPhoto")
            viewModel.clearPhoto()
        }

        viewModel.avatar.observe(viewLifecycleOwner) { avatar ->
            Log.d("MyAppLog", "NewRegUserFragment * load - avatar")
            binding.avatar.load(avatar?.uri.toString())

        }

        //кнопка зарегистрироваться
        binding.signIn.setOnClickListener {
            Log.d("MyAppLog", "NewRegUserFragment * buttom: signIn")

            val login = binding.loginField.text.toString().trim()
            val password = binding.passwordField.text.toString()
            val passwordConfirm = binding.passwordFieldConfirm.text.toString()

            //проверка если поля пустые - выдать сообщение
            if (login.isBlank() || password.isBlank()) {
                Log.d("MyAppLog", "NewRegUserFragment * login / password empty")
                binding.emptyField.isVisible = true
            } else {
                binding.emptyField.isVisible = false
                //проверка на совпадение заполненных полей пароля
                if (password == passwordConfirm) {
                    Log.d("MyAppLog", "NewRegUserFragment * buttom signIn: password confirm")
                    val file = viewModel.avatar.value?.file
                    if (file != null) {
                        viewModel.registerUserWithPhoto(
                            login = login,
                            pass = password,
                            name = login,
                            upload = MediaUpload(file)
                        )
                    } else {
                        viewModel.registerUser(
                            login = login,
                            pass = password,
                            name = login
                        )
                    }

                    binding.incorrect.isVisible = false
                    binding.emptyField.isVisible = false

                    hideKeyboard(binding.root)

                } else {
                    Log.d("MyAppLog", "NewRegUserFragment * buttom signIn: password NO confirm")
                    Toast.makeText(
                        requireContext(),
                        R.string.error_password_confirm,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

        //кнопка обратно
        binding.returnOut.setOnClickListener {
            Log.d("MyAppLog", "NewRegUserFragment * buttom: cancel")
            viewModel.clean()
            findNavController().navigateUp()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("MyAppLog", "NewRegUserFragment * OnBackPressedCallback")
                    viewModel.clean()
                    findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callback
        )

        return binding.root
    }

}
