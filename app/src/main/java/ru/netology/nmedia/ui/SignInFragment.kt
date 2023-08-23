package ru.netology.nmedia.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.internal.ViewUtils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.utils.*
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment: Fragment() {

    //private val viewModel: SignInViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val viewModel: SignInViewModel by viewModels()

    //private val postViewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)
    private val postViewModel: PostViewModel by viewModels()

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.dataState.observe(viewLifecycleOwner) {state ->
            binding.loading.isVisible = state.loading
            binding.signIn.isVisible = !state.loading
            binding.returnOut.isVisible = !state.loading
            binding.incorrect.isVisible = state.error
            binding.loginField.requestFocus()

            if (state.success){
                Log.d("MyAppLog", "SignInUserFragment * state - success")
                postViewModel.refresh()
                viewModel.clean()
                findNavController().navigateUp()
            }

        }

        //кнопка авторизоваться
        binding.signIn.setOnClickListener{
            Log.d("MyAppLog","SingInFragment * buttom: sign in")
            binding.incorrect.isVisible = false
            binding.emptyField.isVisible = false

            hideKeyboard(binding.root)

            val login = binding.loginField.text.toString().trim()
            val password = binding.passwordField.text.toString()

            //проверка если поля пустые - выдать сообщение
            if (login.isBlank() || password.isBlank()){
                binding.emptyField.isVisible = true
            } else {
                viewModel.signIn(login,password)
            }
        }

        //кнопка обратно
        binding.returnOut.setOnClickListener{
            Log.d("MyAppLog","SingInFragment * buttom: cancel")
            viewModel.clean()
            findNavController().navigateUp()
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("MyAppLog", "SignInFragment * OnBackPressedCallback")
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
