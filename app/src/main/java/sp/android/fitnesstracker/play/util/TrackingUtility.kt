package sp.android.fitnesstracker.play.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import sp.android.fitnesstracker.play.services.Polyline
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }


    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (milliseconds < 10) "0" else ""}$milliseconds"
    }

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]
            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }

    fun getBMIMessage(bmi: Float): String {
        return if (bmi <= 18.5) {
            "Your BMI is equal to or less than 18.5 (Underweight)\n\nFor more info visit https://www.bmi-calculator.net"
        } else if (bmi > 18.5 && bmi <= 24.99) {
            "Your BMI is between 18.5 and 24.9 (Normal Weight)\n\nFor more info visit https://www.bmi-calculator.net"
        } else if (bmi >= 25 && bmi <= 29.99) {
            "Your BMI is between 25 and 29.9 (Overweight)\n\nFor more info visit https://www.bmi-calculator.net"
        } else if (bmi >= 30 && bmi <= 34.99) {
            "Your BMI is between 30-34.99 (Obese Class 1)\n\nFor more info visit https://www.bmi-calculator.net"
        } else if (bmi >= 35 && bmi <= 39.99) {
            "Your BMI is between 35-39.99 (Obese Class 2)\n\nFor more info visit https://www.bmi-calculator.net"
        } else {
            "Your BMI is over 40 (Obese Class 3 : Morbid Obesity)\n\nFor more info visit https://www.bmi-calculator.net"
        }
    }
}