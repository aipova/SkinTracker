package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import ru.aipova.skintracker.model.*
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.ui.data.TrackValueData
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

    fun findTracks(startDate: Date, endDate: Date): List<TrackData> {
        Realm.getDefaultInstance().use {
            val tracks = it.where<Track>()
                .between(TrackFields.DATE, startDate, endDate)
                .sort(TrackFields.DATE)
                .findAll()
            return tracks.map { toData(it) }
        }
    }

    private fun toData(track: Track): TrackData {
        val trackValues = track.values.map { trackValue ->
            toData(trackValue.trackType!!, trackValue.value?.toInt() ?: 0)
        }
        return TrackData(
            date = track.date!!,
            values = trackValues.toTypedArray()
        )
    }

    fun getTrackValuesData(date: Date): Array<TrackValueData> {
        val trackTypes = uiRealm.where<TrackType>().findAll()
        val existingValues = getTrackByDate(date, uiRealm)?.values
        return trackTypes.map { trackType ->
            val value = getExistingValueOrZero(existingValues, trackType)
            toData(trackType, value)
        }.toTypedArray()
    }

    private fun toData(
        trackType: TrackType,
        value: Int
    ) =
        TrackValueData(
            trackType.uuid,
            trackType.name,
            trackType.getValueTypeEnum(),
            value,
            trackType.maxValue.toInt()
        )

    private fun getExistingValueOrZero(
        existingValues: RealmList<TrackValue>?,
        trackType: TrackType?
    ): Int {
        val existingValue = existingValues?.find { it.trackType == trackType }
        return existingValue?.value?.toInt() ?: 0
    }

    fun createOrUpdate(date: Date, trackValues: Array<TrackValueData>, callback: CreateTrackCallback) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            val trackForDate = getOrCreateTrack(date, bgRealm)
            val todayTracks = createOrUpdateTrackValues(trackValues, trackForDate, bgRealm)

            trackForDate.apply {
                values.addAll(todayTracks)
            }
            bgRealm.insertOrUpdate(trackForDate)
        }, { callback.onTrackCreated() }, { callback.onError() })
    }

    fun saveNote(date: Date, noteTxt: String, callback: () -> Unit) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            val trackForDate = getOrCreateTrack(date, bgRealm)

            trackForDate.apply {
                note = noteTxt
            }
            bgRealm.insertOrUpdate(trackForDate)
        }, callback)
    }

    private fun getOrCreateTrack(
        trackDate: Date,
        bgRealm: Realm
    ) = getTrackByDate(trackDate, bgRealm) ?: Track().apply { date = trackDate }

    private fun createOrUpdateTrackValues(
        trackValues: Array<TrackValueData>,
        trackForDate: Track,
        realm: Realm
    ): MutableList<TrackValue> {
        val todayTracks = mutableListOf<TrackValue>()
        trackValues.forEach { trackValueData ->
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

    fun createIfNotExists(trackDate: Date, callback: () -> Unit) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            val trackForDate = getTrackByDate(trackDate, bgRealm)
            if (trackForDate == null) {
                val track = Track().apply { date = trackDate }
                bgRealm.insert(track)
            }
        }, callback)
    }
}