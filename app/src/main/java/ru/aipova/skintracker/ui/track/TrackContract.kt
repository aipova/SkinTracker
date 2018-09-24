package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import java.io.File

interface TrackContract {
    interface View: BaseView<Presenter> {
        var isActive: Boolean
        fun showTrackValues(trackValueData: Array<TrackValueData>)
        fun setupNoteText(text: String)
        fun showTrackCreatedMsg()
        fun showCannotCreateTrackMsg()
        fun close()
        fun getTrackValueData(): Array<TrackValueData>
        fun getNote(): String
        fun makePhoto(photoFile: File)
        fun loadPhoto(photoFile: File)
        fun showCannotLoadTrackMsg()
        fun showTrackUpdatedMsg()
    }

    interface Presenter: BasePresenter {
        fun save()
        fun photoCalled()
        fun photoCreated()
    }
}