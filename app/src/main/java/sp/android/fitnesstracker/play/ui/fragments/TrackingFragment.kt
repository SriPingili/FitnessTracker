package sp.android.fitnesstracker.play.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.services.TrackingService
import sp.android.fitnesstracker.play.ui.viewmodels.MainViewModel
import sp.android.fitnesstracker.play.util.Constants.ACTION_START_OR_RESUME_SERVICE

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()

    private var map: GoogleMap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)

        btnToggleRun.setOnClickListener {

            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }

        mapView.getMapAsync {
            map = it
        }
    }

    fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackingService::class.java).let {
            it.action = action
            requireContext().startService(it)
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