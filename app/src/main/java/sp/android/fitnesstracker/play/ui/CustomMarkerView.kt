package sp.android.fitnesstracker.play.ui

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import sp.android.fitnesstracker.play.db.Run
import sp.android.fitnesstracker.play.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pop-up window, when we click on a bar in the bar chart
 */
@SuppressLint("ViewConstructor")
class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]
        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = "Date: ${dateFormat.format(calendar.time)}"

        "${run.avgSpeedInKMH}km/h".also {
            tvAvgSpeed.text = "Speed: $it"
        }
        "${run.distanceInMeters / 1000f}km".also {
            tvDistance.text = "Distance: $it"
        }
        tvDuration.text = "Time: ${TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)}"
        "${run.caloriesBurned}kcal".also {
            tvCaloriesBurned.text = "Calories: $it"
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}