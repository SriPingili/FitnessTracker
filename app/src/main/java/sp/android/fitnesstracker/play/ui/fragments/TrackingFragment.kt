package sp.android.fitnesstracker.play.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.services.TrackingService
import sp.android.fitnesstracker.play.services.polyline
import sp.android.fitnesstracker.play.ui.viewmodels.MainViewModel
import sp.android.fitnesstracker.play.util.Constants.ACTION_PAUSE_SERVICE
import sp.android.fitnesstracker.play.util.Constants.ACTION_START_OR_RESUME_SERVICE
import sp.android.fitnesstracker.play.util.Constants.MAP_ZOOM
import sp.android.fitnesstracker.play.util.Constants.POLYLINE_COLOR
import sp.android.fitnesstracker.play.util.Constants.POLYLINE_WIDTH

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private var map: GoogleMap? = null
    private var isTracking = false
    private var multiLinePoints = mutableListOf<polyline>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener {
            toggleRunButton()
        }

        mapView.getMapAsync {
            map = it
            drawAllPolylines()
        }

        subscribeToObservers()
    }

    private fun toggleRunButton() {
        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if (isTracking) {
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        } else {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        }

    }

    fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).let {
            it.action = action
            requireContext().startService(it)
        }
    }

    fun subscribeToObservers() {

        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.multiLinePoints.observe(viewLifecycleOwner, Observer {
            this.multiLinePoints = it
            drawLatestPolyline()
            moveCameraToUser()
        })
    }


    private fun moveCameraToUser() {
        if (multiLinePoints.isNotEmpty() && multiLinePoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    multiLinePoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun drawAllPolylines() {
        for (polyLine in multiLinePoints) {
            val polylineOptions = PolylineOptions().apply {
                color(POLYLINE_COLOR)
                width(POLYLINE_WIDTH)
                addAll(polyLine)
            }
            map?.addPolyline(polylineOptions)
        }
    }


    private fun drawLatestPolyline() {
        if (multiLinePoints.isNotEmpty() && multiLinePoints.last().size > 1) {
            val preLastLatLng = multiLinePoints.last()[multiLinePoints.last().size - 2]
            val lastLang = multiLinePoints.last().last()

            val polylineOptions = PolylineOptions().apply {
                color(POLYLINE_COLOR)
                width(POLYLINE_WIDTH)
                add(preLastLatLng)
                add(lastLang)
            }

            map?.addPolyline(polylineOptions)
        }
    }


    /*
    *  mapView life cycle methods
    * */
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    //looks like onDestroy() might not be needed, mapview gets
    // destroyed before??
}