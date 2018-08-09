package ru.aipova.skintracker.model.source

import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.TrackFields
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


    fun createNewTrackType(callback: CreateTrackCallback) {
        uiRealm.executeTransactionAsync({ bgRealm ->
            bgRealm.insert(Track().apply {
//                TODO
            })
        }, { callback.onTrackCreated() }, { callback.onError() })
    }

    fun getTrack(trackUid: String): Track? {
        return uiRealm.where<Track>().equalTo(TrackFields.UUID, trackUid).findFirst()
    }

    fun getTracksByDate(date: Date): RealmResults<Track> {
        return uiRealm.where<Track>().equalTo(TrackFields.DATE, date).findAll()
    }

    fun getTrackByDate(date: Date): Track? {
        return uiRealm.where<Track>().equalTo(TrackFields.DATE, date).findFirst()
    }

    fun getAllTracks(): RealmResults<Track> {
        return uiRealm.where<Track>().sort(TrackFields.DATE).findAll()
    }
}