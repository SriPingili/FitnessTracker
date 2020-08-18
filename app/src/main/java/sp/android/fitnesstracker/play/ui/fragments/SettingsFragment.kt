package sp.android.fitnesstracker.play.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.ui.viewmodels.MainViewModel

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: MainViewModel by viewModels()
}