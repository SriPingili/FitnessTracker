package sp.android.fitnesstracker.play.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bmr.*
import kotlinx.android.synthetic.main.fragment_bmr.ageInputId
import kotlinx.android.synthetic.main.fragment_bmr.weightInputId
import kotlinx.android.synthetic.main.fragment_settings.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.util.ActivityLevelConstant
import sp.android.fitnesstracker.play.util.Constants.KEY_ACTIVITY_LEVEL_INDEX
import sp.android.fitnesstracker.play.util.Constants.KEY_AGE
import sp.android.fitnesstracker.play.util.Constants.KEY_GENDER
import sp.android.fitnesstracker.play.util.Constants.KEY_HEIGHT
import sp.android.fitnesstracker.play.util.Constants.KEY_POSITION
import sp.android.fitnesstracker.play.util.Constants.KEY_WEIGHT
import javax.inject.Inject

const val BMR_FRAGMENT_TAG = "CancelDialog"

@AndroidEntryPoint
class BMRFragment : Fragment(R.layout.fragment_bmr) {
    @set:Inject
    var name = "Sri"

    @Inject
    lateinit var sharedPref: SharedPreferences

    lateinit var radioButton: RadioButton

    var age: Int = 0
    var height: Float = 0f
    var weight: Float = 0f
    var gender: String = ""
    lateinit var activityLevelConstantEnum: ActivityLevelConstant


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldsFromSharedPref()

        radioGroupId.setOnCheckedChangeListener { group, checkedId ->
            radioButton = view.findViewById(checkedId)
            gender = radioButton.tag.toString()
            sharedPref.edit().putString(KEY_GENDER, gender).apply()
            Toast.makeText(requireContext(), "${radioButton.tag}", Toast.LENGTH_SHORT).show()
        }

//        activityLevelId.setOnClickListener(View.OnClickListener {
//            ActivityLevelChooserDialog().apply {
//                setActivityLevelListener {
//                    setActivityLevelEnum(it)
//                    displayToast(it)
//                }
//            }.show(parentFragmentManager, BMR_FRAGMENT_TAG)
//        })

        activityLevelId.setOnClickListener {
            ActivityLevelChooserDialog().apply {
                setActivityLevelListener {
                    setActivityLevelEnum(it)
                    displayToast(it)
                }
            }.show(parentFragmentManager, BMR_FRAGMENT_TAG)
        }


        calculateButtonId.setOnClickListener({
            calculateBMR()
        })


    }

    private fun calculateBMR() {
        val success = applyChangesToSharedPref()

        if (success) {


        } else {
            Snackbar.make(
                requireView(),
                "Please fill out all the fields",
                Snackbar.LENGTH_SHORT
            ).show()
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

        if (age > 0) ageInputId.setText(age.toString())
        if (weight > 0f) weightInputId.setText(weight.toString())
        if (height > 0f) heightInputId.setText(height.toString())
        when (gender) {
            getString(R.string.male) -> radioGroupId.check(R.id.maleRadioButtonId)
            getString(R.string.female) -> radioGroupId.check(R.id.femaleRadioButtonId)
        }
        ActivityLevelChooserDialog.position = sharedPref.getInt(KEY_POSITION, 0)
    }

    fun displayToast(position: Int) {
        Toast.makeText(requireContext(), "position = $position", Toast.LENGTH_SHORT).show()
    }

    private fun applyChangesToSharedPref(): Boolean {
        val ageText = ageInputId.text.toString()
        val weightText = weightInputId.text.toString()
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
            viewBMRResultId.visibility = View.VISIBLE

            viewBMRResultId.setText("Dear $name,\n\nYour BMR = ${bmr.toInt()}\n(calories a day needed to keep your body functioning at rest)\n\nCalories needed = ${calories.toInt()}\n(calories a day to maintain body weight)\n\nFor more infor visit https://www.seacoastonline.com/article/20070412/ENTERTAIN/704120346")
        }

        scrollViewId.post(Runnable {
            scrollViewId.scrollTo(0, scrollViewId.bottom)
        })

        return true
    }
}