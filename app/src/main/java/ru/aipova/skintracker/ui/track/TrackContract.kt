package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import java.util.*

interface TrackContract {
    interface View: BaseView<Presenter> {
        var isActive: Boolean
        fun showTrackValues(trackValueData: Array<TrackValueData>)
        fun showTrackCreatedMsg()
        fun showCannotCreateTrackMsg()
        fun close()
        fun getTrackValueData(): Array<TrackValueData>
        fun showCannotLoadTrackMsg()
        fun showTrackUpdatedMsg()
        fun setTitle(date: Date)
    }

    interface Presenter: BasePresenter {
        fun save()
    }
}