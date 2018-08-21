package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import ru.aipova.skintracker.model.*
import ru.aipova.skintracker.ui.track.TrackData
import ru.aipova.skintracker.ui.track.TrackValueData
import java.util.*

class TrackRepository(private val uiRealm: Realm) {

    interface CreateTrackCallback {
        fun onTrackCreated()
        fun onError()
    }

    fun getTrackByDate(date: Date): Track? {
        return getTrackByDate(date, uiRealm)
    }

    private fun getTrackByDate(date: Date, realm: Realm): Track? {
        return realm.where<Track>().equalTo(TrackFields.DATE, date).findFirst()
    }

    fun getTrackValuesData(date: Date): Array<TrackValueData> {
        val trackTypes = uiRealm.where<TrackType>().findAll()
        val existingValues = getTrackByDate(date, uiRealm)?.values
        return trackTypes.map { trackType ->
            val value = getExistingValueOrZero(existingValues, trackType)
            TrackValueData(trackType.uuid, trackType.name ?: "", value)
        }.toTypedArray()
    }

    private fun getExistingValueOrZero(
        existingValues: RealmList<TrackValue>?,
        trackType: TrackType?
    ): Int {
        val existingValue = existingValues?.find { it.trackType == trackType }
        return existingValue?.value?.toInt() ?: 0
    }

    fun createOrUpdate(trackData: TrackData, callback: CreateTrackCallback) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            val trackForDate = getOrCreateTrack(trackData.date, bgRealm)
            val todayTracks = createOrUpdateTrackValues(trackData, trackForDate, bgRealm)

            trackForDate.apply {
                values.addAll(todayTracks)
                note = trackData.note
            }
            bgRealm.insertOrUpdate(trackForDate)
        }, { callback.onTrackCreated() }, { callback.onError() })
    }

    private fun getOrCreateTrack(
        trackDate: Date,
        bgRealm: Realm
    ) = getTrackByDate(trackDate, bgRealm) ?: Track().apply { date = trackDate }

    private fun createOrUpdateTrackValues(
        trackData: TrackData,
        trackForDate: Track,
        realm: Realm
    ): MutableList<TrackValue> {
        val todayTracks = mutableListOf<TrackValue>()
        trackData.values.forEach { trackValueData ->
            val trackType = findTrackTypeByUid(trackValueData.uid, realm)
            val newValue = trackValueData.value.toLong()

            findTrackValue(trackForDate, trackType)?.let {
                it.value = newValue
            } ?: todayTracks.add(newTrackValue(trackType, newValue))
        }
        realm.insertOrUpdate(todayTracks)
        return todayTracks
    }

    private fun findTrackTypeByUid(
        uid: String,
        realm: Realm
    ) = realm.where<TrackType>().equalTo(TrackTypeFields.UUID, uid).findFirst()

    private fun findTrackValue(
        trackForDate: Track,
        trackType: TrackType?
    ) = trackForDate.values.find { it.trackType == trackType }

    private fun newTrackValue(trackType: TrackType?, value: Long): TrackValue {
        return TrackValue().apply {
            this.trackType = trackType
            this.value = value
        }
    }
}