package sp.android.fitnesstracker.play.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.ui.CustomMarkerView
import sp.android.fitnesstracker.play.ui.viewmodels.StatisticsViewModel
import sp.android.fitnesstracker.play.util.TrackingUtility
import java.lang.Math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }

    private fun setupBarChart() {
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
    }

    private fun subscribeToObservers() {
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            // in case DB is empty it will be null
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10) / 10f
                val totalDistanceString = "${totalDistance} km"
                tvTotalDistance.text = totalDistanceString
            }
        })

        viewModel.totalTimeInMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeInMillis = TrackingUtility.getFormattedStopWatchTime(it)
                tvTotalTime.text = totalTimeInMillis
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val roundedAvgSpeed = round(it * 10f) / 10f
                val totalAvgSpeed = "${roundedAvgSpeed} km/h"
                tvAverageSpeed.text = totalAvgSpeed
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCaloriesBurned = "${it} kcal"
                tvTotalCalories.text = totalCaloriesBurned
            }
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }

                val bardataSet = BarDataSet(allAvgSpeeds, "Avg Speed over Time")
                bardataSet.apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                val lineData = BarData(bardataSet)
                barChart.data = lineData
                val marker = CustomMarkerView(
                    it,
                    requireContext(),
                    R.layout.marker_view
                )
                barChart.marker = marker
                barChart.invalidate()
            }
        })
    }
}