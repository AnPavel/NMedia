package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.ui.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.extens.load

@AndroidEntryPoint
class PhotoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        binding.photo.load(requireNotNull(requireArguments().textArg))

        binding.backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        return binding.root
    }
}
