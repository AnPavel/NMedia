package ru.netology.nmedia.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R

object OfferToAuthenticate {
    fun remind(view: View, message: String, fragment: Fragment){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(R.string.sign_in) { findNavController(fragment).navigate(R.id.signInFragment) }
            .show()
    }
}
