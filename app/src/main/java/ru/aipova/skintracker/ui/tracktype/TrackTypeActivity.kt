package ru.aipova.skintracker.ui.tracktype

import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.MenuItemActivity

class TrackTypeActivity : MenuItemActivity<TrackTypeFragment>() {

    override fun createFragment(): TrackTypeFragment {
        return TrackTypeFragment.newInstance()
    }

    override fun setupPresenter(fragment: TrackTypeFragment) {
        TrackTypePresenter(fragment, InjectionStub.trackTypeRepository)
    }
}
