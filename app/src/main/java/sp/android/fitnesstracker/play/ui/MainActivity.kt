package sp.android.fitnesstracker.play.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import sp.android.fitnesstracker.play.R
import sp.android.fitnesstracker.play.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(toolbar)
        bottomNavigationView.setupWithNavController(navHostFragmentId.findNavController())
        bottomNavigationView.setOnNavigationItemReselectedListener {
            /*do nothing*/
        }

        navHostFragmentId.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.runFragment, R.id.statisticsFragment, R.id.BMRFragment, R.id.BMIFragment -> bottomNavigationView.visibility =
                        View.VISIBLE
                    else -> bottomNavigationView.visibility = View.GONE
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsFragment) {
            navHostFragmentId.findNavController().navigate(R.id.action_global_settingsFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //Checks if we launched the activity from the notification
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            navHostFragmentId.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }
}