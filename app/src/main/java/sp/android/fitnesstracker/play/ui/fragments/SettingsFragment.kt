package sp.android.fitnesstracker.play.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.util.Constants.KEY_NAME
import sp.android.fitnesstracker.play.util.Constants.KEY_WEIGHT
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    @Inject
    lateinit var sharedPref: SharedPreferences
/*
    this depends on the values we start the app with. But we want to have the up to date values, since they can change during runtime

    @set:Inject
    var name = "Sri"
    @set:Inject
    var weight = 80f*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()

        btnApplyChanges.setOnClickListener {
            val success = applyChangesToSharedPref()
            if (success) {
                Snackbar.make(requireView(), "Saved changes", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(
                    requireView(),
                    "Please fill out all the fields",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPref.getString(KEY_NAME, "")
        val weight = sharedPref.getFloat(KEY_WEIGHT, 175f)
        nameInputId.setText(name)
        weightInputEditText.setText(weight.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {
        val nameText = nameInputId.text.toString()
        val weightText = weightInputEditText.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
        val toolbarText = "Let's go, $nameText!"
        requireActivity().tvToolbarTitle.text = toolbarText
        findNavController().navigate(R.id.action_settingsFragment_to_runFragment)
        return true
    }
}