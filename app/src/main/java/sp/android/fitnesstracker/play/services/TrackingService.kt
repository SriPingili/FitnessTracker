package sp.android.fitnesstracker.play.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.ui.MainActivity
import sp.android.fitnesstracker.play.util.Constants
import sp.android.fitnesstracker.play.util.Constants.ACTION_PAUSE_SERVICE
import sp.android.fitnesstracker.play.util.Constants.ACTION_START_OR_RESUME_SERVICE
import sp.android.fitnesstracker.play.util.Constants.ACTION_STOP_SERVICE
import sp.android.fitnesstracker.play.util.Constants.FASTEST_LOCATION_INTERVAL
import sp.android.fitnesstracker.play.util.Constants.LOCATION_UPDATE_INTERVAL
import sp.android.fitnesstracker.play.util.Constants.NOTIFICATION_CHANNEL_ID
import sp.android.fitnesstracker.play.util.Constants.NOTIFICATION_ID
import sp.android.fitnesstracker.play.util.TrackingUtility
import timber.log.Timber


typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

class TrackingService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val multiLinePoints = MutableLiveData<polylines>()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        multiLinePoints.postValue(mutableListOf())
    }

    private fun addEmptyPolyline() {
        multiLinePoints.value?.apply {
            val emptyPolyLine: polyline = mutableListOf()
            add(emptyPolyLine)
            multiLinePoints.postValue(this)
        } ?: multiLinePoints.postValue(mutableListOf(mutableListOf()))
    }

    //adds to the last
    private fun addPathPoint(location: Location?) {
        location?.apply {
            val latLong = LatLng(latitude, longitude)

            multiLinePoints.value?.apply {
                last().add(latLong)
                multiLinePoints.postValue(this)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {

            updateLocationTracking(it)

        })
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                    } else {
                        startForegroundService()
                        Timber.d("Resuming service")
                    }

                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("Paused service")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                }
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isTracking.postValue(false)
    }

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }


    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }


    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("New Location with latitude = ${location.latitude} and longitude = ${location.longitude}")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}