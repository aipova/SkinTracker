package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.statistics.BasePresenter

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
        fun makePhoto(filename: String)
        fun loadPhoto(photoFileName: String)
        fun showCannotLoadTrackMsg()
    }

    interface Presenter: BasePresenter {
        fun save()
        fun photoCalled()
        fun photoCreated()
    }
}