package ru.aipova.skintracker.ui.trackvalues

import android.content.Context
import android.content.Intent
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.SingleFragmentActivity
import java.util.*

class TrackValuesActivity : SingleFragmentActivity<TrackValuesFragment>() {
    override fun createFragment(): TrackValuesFragment {
        return TrackValuesFragment.newInstance()
    }

    private fun getDateExtra() = intent.getSerializableExtra(DATE_PARAMETER) as Date

    override fun setupPresenter(fragment: TrackValuesFragment) {
        TrackValuesPresenter(fragment, getDateExtra(), InjectionStub.trackRepository)
    }

    companion object {
        private const val DATE_PARAMETER = "date"
        fun createIntent(context: Context, date: Date): Intent {
            return Intent(context, TrackValuesActivity::class.java).putExtra(DATE_PARAMETER, date)
        }
    }
}
