package sp.android.fitnesstracker.play.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_bmr.calculateButtonId
import kotlinx.android.synthetic.main.fragment_bmr.heightInputId
import kotlinx.android.synthetic.main.fragment_bmr.scrollViewId
import kotlinx.android.synthetic.main.fragment_bmr.viewBMRResultId
import kotlinx.android.synthetic.main.fragment_bmr.weightInputEditText
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.util.Constants
import sp.android.fitnesstracker.play.util.TrackingUtility
import java.lang.Math.round
import javax.inject.Inject

@AndroidEntryPoint
class BMIFragment : Fragment(R.layout.fragment_bmi) {
    @set:Inject
    var name = "Sri"

    @Inject
    lateinit var sharedPref: SharedPreferences
    var height: Float = 0f
    var weight: Float = 0f


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFieldsFromSharedPref()




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


    private fun loadFieldsFromSharedPref() {
        weight = sharedPref.getFloat(Constants.KEY_WEIGHT, 0f)
        height = sharedPref.getFloat(Constants.KEY_HEIGHT, 0f)

        if (weight > 0f) weightInputEditText.setText(weight.toString())
        if (height > 0f) heightInputId.setText(height.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {
        val weightText = weightInputEditText.text.toString()
        val heightText = heightInputId.text.toString()


        if (weightText.isEmpty() || heightText.isEmpty()) {
            return false
        }

        val weight = weightText.toFloat()
        val height = heightText.toFloat()


        sharedPref.edit()
            .putFloat(Constants.KEY_HEIGHT, height)
            .apply()

//        var bmi = (weight / (height * height)) * 703

        var bmi = round(((weight / (height * height)) * 703) *100)/100f




        viewBMRResultId.visibility = View.VISIBLE

        viewBMRResultId.setText(
            "Dear $name,\n\nYou have a BMI of ${bmi}.\n\n${TrackingUtility.getBMIMessage(
                bmi
            )}"
        )

        scrollViewId.post({
            scrollViewId.scrollTo(0, scrollViewId.bottom)
        })

        return true
    }
}