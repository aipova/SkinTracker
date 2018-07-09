package ru.aipova.skintracker.ui.tracktype

import android.content.Context
import android.content.Intent
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.SingleFragmentActivity

class TrackTypeActivity : SingleFragmentActivity<TrackTypeFragment>() {

    override fun createFragment(): TrackTypeFragment {
        return TrackTypeFragment.newInstance()
    }

    override fun setupPresenter(fragment: TrackTypeFragment) {
        TrackTypePresenter(fragment, InjectionStub.trackTypeRepository)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, TrackTypeActivity::class.java)
        }
    }
}
