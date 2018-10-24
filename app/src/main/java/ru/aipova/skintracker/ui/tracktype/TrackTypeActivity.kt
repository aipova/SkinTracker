package ru.aipova.skintracker.ui.tracktype

import ru.aipova.skintracker.ui.DaggerMenuItemActivity

class TrackTypeActivity : DaggerMenuItemActivity<TrackTypeFragment>() {
    override fun createFragment(): TrackTypeFragment {
        return TrackTypeFragment.newInstance()
    }
}
