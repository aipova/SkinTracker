package ru.aipova.skintracker.ui.tracktype

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.SingleFragmentActivity

class TrackTypeActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        val fragment = TrackTypeFragment.newInstance()
        TrackTypePresenter(fragment, InjectionStub.trackTypeRepository)
        return fragment
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, TrackTypeActivity::class.java)
        }
    }
}
