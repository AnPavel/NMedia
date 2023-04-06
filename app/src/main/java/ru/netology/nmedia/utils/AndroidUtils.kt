package ru.netology.nmedia.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/*
object AndroidUtils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

*/

class AndroidUtils {

    fun hideKeyboard(view: View) {
        val imn = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun showKeyboard(view: View) {
        val imn = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT)
    }

}
