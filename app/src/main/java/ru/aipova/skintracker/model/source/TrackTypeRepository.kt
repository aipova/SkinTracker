package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import ru.aipova.skintracker.model.*

class TrackTypeRepository(private val uiRealm: Realm) {

    interface CreateTrackTypeCallback {
        fun onTrackTypeCreated()
        fun onError()
    }

    fun removeTrackType(trackTypeUid: String) {
        uiRealm.executeTransactionAsync { bgRealm ->
            val trackType =
                bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            val tracks =
                bgRealm.where<TrackValue>().equalTo(TrackValueFields.TRACK_TYPE.UUID, trackTypeUid)
                    .findAll()
            tracks.deleteAllFromRealm()
            trackType?.deleteFromRealm()
        }
    }

    fun editTrackType(
        trackTypeUid: String,
        trackTypeName: String,
        valueType: ValueType,
        min: Int,
        max: Int
    ) {
        uiRealm.executeTransactionAsync { bgRealm ->
            val trackTypeManaged =
                bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackTypeManaged?.apply {
                name = trackTypeName
                setValueTypeEnum(valueType)
                maxValue = max.toLong()
                minValue = min.toLong()
            }
        }
    }

    fun createNewTrackType(
        trackTypeName: String, valueType: ValueType,
        min: Int,
        max: Int, callback: CreateTrackTypeCallback
    ) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            bgRealm.insert(TrackType().apply {
                name = trackTypeName
                setValueTypeEnum(valueType)
                maxValue = max.toLong()
                minValue = min.toLong()
            })
        }, { callback.onTrackTypeCreated() }, { callback.onError() })
    }

    fun getEditableTrackTypesAsync(): RealmResults<TrackType> {
        return uiRealm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }

    fun getChartTrackTypeNames(): List<String> {
        return Realm.getDefaultInstance().use {
            it.where<TrackType>()
                .`in`(TrackTypeFields.VALUE_TYPE, arrayOf(ValueType.SEEK.name, ValueType.AMOUNT.name))
                .findAll()
                .map { it.name }
        }
    }
}