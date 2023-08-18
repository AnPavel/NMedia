package ru.netology.nmedia.activity

import android.app.Dialog
import android.os.Bundle
import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject


/*
@AndroidEntryPoint
class LogoutDialog @Inject constructor(
    private val appAuth: AppAuth
): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * yes")
                appAuth.removeAuth()
                dialog.dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * no")
                dialog.dismiss()
            }
            .create()

    companion object {
        const val TAG = "LogoutDialogFragment"
    }

}

 */


@AndroidEntryPoint
class LogoutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * yes")
                //AppAuth.getInstance().removeAuth()
                return@setPositiveButton
                dialog.dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * no")
                return@setNegativeButton
                dialog.dismiss()
            }
            .create()

}
