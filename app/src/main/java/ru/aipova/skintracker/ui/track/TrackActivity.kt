package ru.aipova.skintracker.ui.track

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import ru.aipova.skintracker.ui.SingleFragmentActivity

class TrackActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return TrackFragment.newInstance()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, TrackActivity::class.java)
        }
    }
}
