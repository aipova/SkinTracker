package ru.aipova.skintracker

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_NAME
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_TYPE_UID

class SkinTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build())

//        TODO set realm initial data
        getSkinQualityTrackType()
    }

    private fun getSkinQualityTrackType() {
        Realm.getDefaultInstance().use { r ->
            r.executeTransaction { realm ->
                realm.insertOrUpdate(skinQualityTrackType)
            }
        }
    }

    companion object {
        var skinQualityTrackType: TrackType = TrackType().apply {
            uuid = SKIN_QUALITY_TRACK_TYPE_UID
            name = SKIN_QUALITY_TRACK_NAME
            removable = false
        }
    }
}