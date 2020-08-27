package sp.android.fitnesstracker.play.ui

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.marker_view.view.*
import sp.android.fitnesstracker.play.R
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
    context: Context,
    layoutId: Int
) : MarkerView(context, layoutId) {

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        if (entry == null) {
            return
        }
        val curRunId = entry.x.toInt()
        val run = runs[curRunId]
        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat =
            SimpleDateFormat(context.getString(R.string.data_format), Locale.getDefault())
        tvDate.text =
            String.format(context.getString(R.string.date), dateFormat.format(calendar.time))

        String.format(context.getString(R.string.total_avg_speed), run.avgSpeedInKMH).also {
            tvAvgSpeed.text = String.format(context.getString(R.string.speed), it)
        }
        String.format(context.getString(R.string.total_distance), run.distanceInMeters / 1000f)
            .also {
                tvDistance.text = String.format(context.getString(R.string.distance), it)
            }
        tvDuration.text = String.format(
            context.getString(R.string.time),
            TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
        )
        String.format(context.getString(R.string.total_calories), run.caloriesBurned).also {
            tvCaloriesBurned.text = String.format(context.getString(R.string.calories), it)
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}