package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.kotlin.where
import ru.aipova.skintracker.model.*
import ru.aipova.skintracker.ui.track.TrackData
import java.util.*

class TrackRepository(private val uiRealm: Realm) {

    interface CreateTrackCallback {
        fun onTrackCreated()
        fun onError()
    }

    fun removeTrack(trackUid: String) {
        uiRealm.executeTransactionAsync { bgRealm ->
            val track = bgRealm.where<Track>().equalTo(TrackFields.UUID, trackUid).findFirst()
            track?.deleteFromRealm()
        }
    }

    fun getTrackByDate(date: Date): Track? {
        return uiRealm.where<Track>().equalTo(TrackFields.DATE, date).findFirst()
    }

    fun createOrUpdate(trackData: TrackData, callback: CreateTrackCallback) {
        val todayTracks = mutableListOf<TrackValue>()
        uiRealm.executeTransactionAsync({ bgRealm ->
            val trackForDate =
                bgRealm.where<Track>().equalTo(TrackFields.DATE, trackData.date).findFirst()
                        ?: Track().apply { date = trackData.date }
            val existingTrackValues = trackForDate.values

            trackData.values.forEach { trackValueData ->
                val trackValue = TrackValue().apply {
                    trackType = bgRealm.where<TrackType>()
                        .equalTo(TrackTypeFields.UUID, trackValueData.uid).findFirst()
                    value = trackValueData.value.toLong()
                }

                val existingTrackValue =
                    existingTrackValues.find { it.trackType == trackValue.trackType }
                existingTrackValue?.let {
                    it.value = trackValue.value
                } ?: todayTracks.add(trackValue)
            }

            bgRealm.insertOrUpdate(todayTracks)
            trackForDate.values.addAll(todayTracks)
            trackForDate.note = trackData.note
            bgRealm.insertOrUpdate(trackForDate)
        }, { callback.onTrackCreated() }, { callback.onError() })
    }

}