package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackTypeFields
import ru.aipova.skintracker.model.TrackValue
import ru.aipova.skintracker.model.TrackValueFields

class TrackTypeRepository(private val uiRealm: Realm) {

    interface CreateTrackTypeCallback {
        fun onTrackTypeCreated()
        fun onError()
    }

    fun removeTrackType(trackTypeUid: String) {
        uiRealm.executeTransactionAsync { bgRealm ->
            val trackType = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            val tracks = bgRealm.where<TrackValue>().equalTo(TrackValueFields.TRACK_TYPE.UUID, trackTypeUid).findAll()
            tracks.deleteAllFromRealm()
            trackType?.deleteFromRealm()
        }
    }

    fun editTrackTypeName(trackTypeUid: String, trackTypeName: String) {
        uiRealm.executeTransactionAsync { bgRealm ->
            val trackTypeManaged =
                bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackTypeManaged?.name = trackTypeName
        }
    }

    fun createNewTrackType(trackTypeName: String, callback: CreateTrackTypeCallback) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            bgRealm.insert(TrackType().apply {
                name = trackTypeName
            })
        }, { callback.onTrackTypeCreated() }, { callback.onError() })
    }

    fun getEditableTrackTypesAsync(): RealmResults<TrackType> {
        return uiRealm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }

    fun getTrackTypesSnapshot(): List<TrackType> {
//        TODO add ordering
        return uiRealm.where<TrackType>().findAll().createSnapshot()
    }
}