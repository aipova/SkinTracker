package ru.aipova.skintracker.ui.track

import android.content.Context
import android.content.Intent
import ru.aipova.skintracker.ui.SingleFragmentActivity

class TrackActivity : SingleFragmentActivity<TrackFragment>() {
    override fun createFragment(): TrackFragment {
        return TrackFragment.newInstance()
    }

    override fun setupPresenter(fragment: TrackFragment) {
//         TODO no presenter yet
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, TrackActivity::class.java)
        }
    }
}
