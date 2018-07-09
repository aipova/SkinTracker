package ru.aipova.skintracker.ui.tracktype

import io.realm.RealmResults
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.statistics.BasePresenter

interface TrackTypeContract {
    interface View: BaseView<Presenter> {
        val isActive: Boolean
        fun initTrackTypesView(trackTypes: RealmResults<TrackType>)
        fun showTrackTypeCreatedMsg(trackTypeName: String)
        fun showCannotCreateTrackTypeMsg()
        fun showNoTrackTypesView()
        fun showTrackTypesView()
    }

    interface Presenter: BasePresenter {
        fun createNewTrackType(trackTypeName: String)
        fun editTrackTypeName(trackTypeUid: String, trackTypeName: String)
        fun removeTrackType(trackTypeUid: String)
        fun stop()
    }
}