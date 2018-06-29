package ru.aipova.skintracker

import android.support.v4.app.Fragment

class StatisticsActivity : SingleFragmentActivity(), StatisticsFragment.Callbacks {
    override fun onCreateNewTrack() {
        startActivity(TrackActivity.createIntent(this))
    }

    override fun createFragment(): Fragment {
        return StatisticsFragment.newInstance()
    }
}
