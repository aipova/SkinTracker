package ru.aipova.skintracker.ui.statistics

import ru.aipova.skintracker.ui.SingleFragmentActivity
import ru.aipova.skintracker.ui.track.TrackActivity
import ru.aipova.skintracker.ui.trackpager.TrackPagerActivity
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity

class StatisticsActivity : SingleFragmentActivity<StatisticsFragment>(),
    StatisticsFragment.Callbacks {
    override fun onShowDiary() {
        startActivity(TrackPagerActivity.createIntent(this))
    }

    override fun onCreateNewTrack() {
        startActivity(TrackActivity.createIntent(this))
    }

    override fun onShowTrackTypes() {
        startActivity(TrackTypeActivity.createIntent(this))
    }

    override fun createFragment(): StatisticsFragment {
        return StatisticsFragment.newInstance()
    }

    override fun setupPresenter(fragment: StatisticsFragment) {
        //TODO no presenter yet
    }
}
