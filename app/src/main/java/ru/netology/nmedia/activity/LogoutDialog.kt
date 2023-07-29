package ru.netology.nmedia.activity

import android.app.Dialog
import android.os.Bundle
import android.app.AlertDialog
import android.util.Log
import androidx.fragment.app.DialogFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth

class LogoutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * yes")
                AppAuth.getInstance().removeAuth()
                dialog.dismiss()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                Log.d("MyAppLog", "LogoutDialog * no")
                dialog.dismiss()
            }
            .create()

}
