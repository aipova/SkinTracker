package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.statistics.BasePresenter

interface TrackContract {
    interface View: BaseView<Presenter> {

        fun initTrackValues(trackValueData: Array<TrackValueData>)
        fun setupNoteText(text: String)
        fun showTrackCreatedMsg()
        fun showCannotCreateTrackMsg()
    }

    interface Presenter: BasePresenter {

        fun getPhotoFileName(): String
        fun saveTrackData(
            trackValueDataArray: Array<TrackValueData>,
            note: String
        )
    }
}