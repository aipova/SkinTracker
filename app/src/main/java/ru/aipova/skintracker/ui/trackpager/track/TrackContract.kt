package ru.aipova.skintracker.ui.trackpager.track

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.ui.data.TrackValueData
import java.io.File
import java.util.*

interface TrackContract {
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
        fun onUpdateNote(trackData: TrackData)
        fun init(date: Date)
        fun takeView(view: View)
        fun dropView()
    }
}