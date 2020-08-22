package sp.android.fitnesstracker.play.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sp.android.fitnesstracker.play.db.Run
import sp.android.fitnesstracker.play.repositories.MainRepository

class MainViewModel @ViewModelInject constructor(
    val repository: MainRepository
) : ViewModel() {

    fun runsSortedByDate() = repository.getAllRunsSortedByDate()

    fun insertRun(run: Run) = viewModelScope.launch {
        repository.insertRun(run)
    }
}