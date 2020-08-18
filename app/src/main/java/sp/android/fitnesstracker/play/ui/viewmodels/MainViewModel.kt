package sp.android.fitnesstracker.play.ui.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import sp.android.fitnesstracker.play.repositories.MainRepository

class MainViewModel @ViewModelInject constructor(
    val repository: MainRepository
) : ViewModel() {

}