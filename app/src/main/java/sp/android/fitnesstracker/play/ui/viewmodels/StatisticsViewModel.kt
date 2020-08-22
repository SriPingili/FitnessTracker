package sp.android.fitnesstracker.play.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import sp.android.fitnesstracker.play.repositories.MainRepository

class StatisticsViewModel @ViewModelInject constructor(
    val repository: MainRepository
) : ViewModel() {

    var totalDistance = repository.getTotalDistance()
    var totalTimeInMillis = repository.getTotalTimeInMillis()
    var totalAvgSpeed = repository.getTotalAvgSpeed()
    var totalCaloriesBurned = repository.getTotalCaloriesBurned()

    var runsSortedByDate = repository.getAllRunsSortedByDate()
}