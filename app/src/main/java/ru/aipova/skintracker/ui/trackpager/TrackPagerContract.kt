package ru.aipova.skintracker.ui.trackpager

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.track.TrackValueData
import java.io.File

interface TrackPagerContract {
    interface View: BaseView<Presenter> {
        var isActive: Boolean
        fun showTrackValues(trackValueData: Array<TrackValueData>)
        fun setupNoteText(text: String)
        fun loadPhoto(photoFile: File)
        fun setupTrackExists(isExisting: Boolean)
        fun showPhotoView()
        fun hidePhotoView()
        fun showNoteView()
    }

    interface Presenter: BasePresenter {
        fun init()
    }
}