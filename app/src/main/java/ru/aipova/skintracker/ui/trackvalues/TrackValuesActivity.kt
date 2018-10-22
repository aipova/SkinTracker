package ru.aipova.skintracker.ui.trackvalues

import android.content.Context
import android.content.Intent
import ru.aipova.skintracker.ui.DaggerSingleFragmentActivity
import java.util.*

class TrackValuesActivity : DaggerSingleFragmentActivity<TrackValuesFragment>() {
    override fun createFragment(): TrackValuesFragment {
        return TrackValuesFragment.newInstance(getDateExtra())
    }

    private fun getDateExtra() = intent.getSerializableExtra(DATE_PARAMETER) as Date

    companion object {
        private const val DATE_PARAMETER = "date"
        fun createIntent(context: Context, date: Date): Intent {
            return Intent(context, TrackValuesActivity::class.java).putExtra(DATE_PARAMETER, date)
        }
    }
}
