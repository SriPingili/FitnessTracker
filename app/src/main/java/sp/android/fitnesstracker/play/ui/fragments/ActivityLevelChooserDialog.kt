package sp.android.fitnesstracker.play.ui.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import sp.android.fitnesstracker.play.R


class ActivityLevelChooserDialog : DialogFragment() {

    companion object {
        var position: Int = 0
    }

    private var activityLevelListener: ((Int) -> Unit)? = null

    fun setActivityLevelListener(listener: ((Int) -> Unit)) {
        activityLevelListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                position = which
            }

        }

        val list = context?.resources?.getStringArray(R.array.actvity_level_choice)

        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Choose your Activity Level")
            .setSingleChoiceItems(list, position, listener)
            .setPositiveButton("OK") { _, _ ->
                activityLevelListener?.let { yes ->
                    yes(position)
                }

            }
            .setNegativeButton("CANCEL") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }
}