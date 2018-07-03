package ru.aipova.skintracker.ui.statistics

import android.support.v4.app.Fragment
import ru.aipova.skintracker.ui.SingleFragmentActivity
import ru.aipova.skintracker.ui.track.TrackActivity
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity

class StatisticsActivity : SingleFragmentActivity(),
    StatisticsFragment.Callbacks {
    override fun onCreateNewTrack() {
        startActivity(TrackActivity.createIntent(this))
    }

    override fun onShowTrackTypes() {
        startActivity(TrackTypeActivity.createIntent(this))
    }

    override fun createFragment(): Fragment {
        return StatisticsFragment.newInstance()
    }
}
