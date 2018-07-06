package ru.aipova.skintracker.ui.tracktype

import io.realm.RealmResults
import io.realm.kotlin.where
import ru.aipova.skintracker.RealmManager.realm
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackTypeFields

class TrackTypePresenter(private val trackTypeView: TrackTypeContract.View) : TrackTypeContract.Presenter {
    override fun removeTrackType(trackTypeUid: String) {
        //         TODO remove all tracks or mark removed?
        realm.executeTransactionAsync { bgRealm ->
            val trackType = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackType?.deleteFromRealm()
        }
    }

    override fun editTrackTypeName(trackTypeUid: String, trackTypeName: String) {
        realm.executeTransactionAsync { bgRealm ->
            val trackTypeManaged = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackTypeManaged?.name = trackTypeName
        }
    }

    //    TODO move realm calls to repository
    override fun createNewTrackType(trackTypeName: String) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.insert(TrackType().apply {
                name = trackTypeName
            })
        }
        trackTypeView.showTrackTypeCreatedNotification(trackTypeName)
    }

    init {
        trackTypeView.presenter = this
    }

    override fun start() {
        trackTypeView.initTrackTypesView(allTrackTypesAsync())
    }

    private fun allTrackTypesAsync(): RealmResults<TrackType> {
        return realm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }
}