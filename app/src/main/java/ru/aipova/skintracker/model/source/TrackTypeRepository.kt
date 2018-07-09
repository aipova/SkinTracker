package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackTypeFields

class TrackTypeRepository(private val uiRealm: Realm) {
    fun removeTrackType(trackTypeUid: String) {
        //         TODO remove all tracks or mark removed?
        uiRealm.executeTransactionAsync { bgRealm ->
            val trackType = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackType?.deleteFromRealm()
        }
    }

    fun editTrackTypeName(trackTypeUid: String, trackTypeName: String) {
        uiRealm.executeTransactionAsync { bgRealm ->
            val trackTypeManaged = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackTypeManaged?.name = trackTypeName
        }
    }

    fun createNewTrackType(trackTypeName: String) {
//        TODO callbacks if exception
        uiRealm.executeTransactionAsync { bgRealm ->
            bgRealm.insert(TrackType().apply {
                name = trackTypeName
            })
        }
    }

    fun getAllTrackTypesAsync(): RealmResults<TrackType> {
        return uiRealm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }
}