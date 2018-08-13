package ru.aipova.skintracker.ui.track

import android.content.Context
import android.content.Intent
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.SingleFragmentActivity
import java.util.*

class TrackActivity : SingleFragmentActivity<TrackFragment>() {
    override fun createFragment(): TrackFragment {
        return TrackFragment.newInstance()
    }

    private fun getDateExtra() = intent.getSerializableExtra(DATE_PARAMETER) as Date

    override fun setupPresenter(fragment: TrackFragment) {
        TrackPresenter(fragment, getDateExtra(), InjectionStub.trackRepository, InjectionStub.trackTypeRepository)
    }

    companion object {
        private const val DATE_PARAMETER = "date"
        fun createIntent(context: Context, date: Date): Intent {
            return Intent(context, TrackActivity::class.java).putExtra(DATE_PARAMETER, date)
        }
    }
}
