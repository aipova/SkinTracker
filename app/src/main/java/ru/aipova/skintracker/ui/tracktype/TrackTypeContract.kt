package ru.aipova.skintracker.ui.tracktype

import io.realm.RealmResults
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.ValueType
import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView

interface TrackTypeContract {
    interface View: BaseView<Presenter> {
        fun initTrackTypesView(trackTypes: RealmResults<TrackType>)
        fun showTrackTypeCreatedMsg(trackTypeName: String)
        fun showCannotCreateTrackTypeMsg()
        fun showNoTrackTypesView()
        fun showTrackTypesView()
    }

    interface Presenter: BasePresenter {
        fun createNewTrackType(
            trackTypeName: String,
            selectedValueType: ValueType,
            min: Int,
            max: Int
        )
        fun editTrackTypeName(
            trackTypeUid: String,
            trackTypeName: String,
            selectedValueType: ValueType,
            min: Int,
            max: Int
        )
        fun removeTrackType(trackTypeUid: String)
        fun stop()
        fun takeView(view: View)
        fun dropView()
    }
}