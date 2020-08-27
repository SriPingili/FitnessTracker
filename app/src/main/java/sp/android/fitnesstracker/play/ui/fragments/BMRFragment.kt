package sp.android.fitnesstracker.play.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bmr.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.util.ActivityLevelConstant
import sp.android.fitnesstracker.play.util.Constants.KEY_AGE
import sp.android.fitnesstracker.play.util.Constants.KEY_GENDER
import sp.android.fitnesstracker.play.util.Constants.KEY_HEIGHT
import sp.android.fitnesstracker.play.util.Constants.KEY_POSITION
import sp.android.fitnesstracker.play.util.Constants.KEY_WEIGHT
import javax.inject.Inject

const val BMR_FRAGMENT_TAG = "CancelDialog"

/*
*
* Fragment responsible for calculating BMR based on the info provided
* by the user
* */
@AndroidEntryPoint
class BMRFragment : Fragment(R.layout.fragment_bmr) {
    @set:Inject
    var name = ""

    @Inject
    lateinit var sharedPref: SharedPreferences
    lateinit var radioButton: RadioButton
    lateinit var activityLevelConstantEnum: ActivityLevelConstant
    var age: Int = 0
    var height: Float = 0f
    var weight: Float = 0f
    var gender: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()

        radioGroupId.setOnCheckedChangeListener { group, checkedId ->
            radioButton = view.findViewById(checkedId)
            gender = radioButton.tag.toString()
            sharedPref.edit().putString(KEY_GENDER, gender).apply()
            Toast.makeText(requireContext(), "${radioButton.tag}", Toast.LENGTH_SHORT).show()
        }

        activityLevelId.setOnClickListener {
            ActivityLevelChooserDialog().apply {
                setActivityLevelListener {
                    setActivityLevelEnum(it)
                }
            }.show(parentFragmentManager, BMR_FRAGMENT_TAG)
        }

        calculateButtonId.setOnClickListener {
            if (!applyChangesToSharedPref()) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.please_enter_all_fields),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setActivityLevelEnum(index: Int) {
        if (index <= 4) {
            sharedPref.edit().putInt(KEY_POSITION, index).apply()
        }
        return when (index) {
            0 -> activityLevelConstantEnum = ActivityLevelConstant.SEDENTARY
            1 -> activityLevelConstantEnum = ActivityLevelConstant.LIGHTLY_ACTIVE
            2 -> activityLevelConstantEnum = ActivityLevelConstant.MODERATELY_ACTIVE
            3 -> activityLevelConstantEnum = ActivityLevelConstant.VERY_ACTIVE
            4 -> activityLevelConstantEnum = ActivityLevelConstant.EXTRA_ACTIVE
            else -> activityLevelConstantEnum = ActivityLevelConstant.NONE
        }
    }

    private fun loadFieldsFromSharedPref() {
        val index = sharedPref.getInt(KEY_POSITION, 5)

        age = sharedPref.getInt(KEY_AGE, 0)
        weight = sharedPref.getFloat(KEY_WEIGHT, 0f)
        height = sharedPref.getFloat(KEY_HEIGHT, 0f)
        gender = sharedPref.getString(KEY_GENDER, "").toString()
        setActivityLevelEnum(index)

        if (age > 0) ageInputEditText.setText(age.toString())
        if (weight > 0f) weightInputEditText.setText(weight.toString())
        if (height > 0f) heightInputId.setText(height.toString())
        when (gender) {
            getString(R.string.male) -> radioGroupId.check(R.id.maleRadioButtonId)
            getString(R.string.female) -> radioGroupId.check(R.id.femaleRadioButtonId)
        }
        ActivityLevelChooserDialog.position = sharedPref.getInt(KEY_POSITION, 0)
    }

    private fun applyChangesToSharedPref(): Boolean {
        val ageText = ageInputEditText.text.toString()
        val weightText = weightInputEditText.text.toString()
        val heightText = heightInputId.text.toString()


        if (ageText.isEmpty() || weightText.isEmpty() || heightText.isEmpty() || gender.isEmpty() || activityLevelConstantEnum == ActivityLevelConstant.NONE) {
            return false
        }

        val age = ageText.toInt()
        val weight = weightText.toFloat()
        val height = heightText.toFloat()


        sharedPref.edit()
            .putInt(KEY_AGE, age)
            .putFloat(KEY_HEIGHT, height)
            .apply()

        var bmr = 0f
        var calories = 0f

        when (gender) {
            getString(R.string.male) -> {
                bmr = (66 + (6.23 * weight) + (12.7 * height) - (6.8 * age)).toFloat()
                calories = bmr * activityLevelConstantEnum.activityConstant
            }
            getString(R.string.female) -> {
                bmr = (655 + (4.35 * weight) + (4.7 * height) - (4.7 * age)).toFloat()
                calories = bmr * activityLevelConstantEnum.activityConstant
            }
        }

        if (bmr > 0f) {
            MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(String.format(getString(R.string.dear_user), name))
                .setMessage(
                    String.format(
                        getString(R.string.bmr_result),
                        bmr.toInt(),
                        calories.toInt()
                    )
                )
                .setPositiveButton(getString(android.R.string.ok)) { dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .create()
                .show()
        }

        return true
    }
}