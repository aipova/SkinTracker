package ru.aipova.skintracker.ui.trackvalues

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.data.TrackValueData
import java.util.*

interface TrackValuesContract {
    interface View: BaseView<Presenter> {
        var isActive: Boolean
        fun showTrackValues(trackValueData: Array<TrackValueData>)
        fun showTrackValuesCreatedMsg()
        fun showCannotSaveTrackValuesMsg()
        fun close()
        fun getTrackValueData(): Array<TrackValueData>
        fun showCannotLoadTrackMsg()
        fun showTrackValuesUpdatedMsg()
        fun setTitle(date: Date)
    }

    interface Presenter: BasePresenter {
        fun save()
    }
}