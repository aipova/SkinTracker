package ru.aipova.skintracker

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_NAME
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_TYPE_UID
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackTypeRepository
import ru.aipova.skintracker.utils.PhotoUtils

class SkinTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(getRealmConfiguration())

        InjectionStub.realm = Realm.getDefaultInstance()
        InjectionStub.trackTypeRepository = TrackTypeRepository(InjectionStub.realm)
        InjectionStub.trackRepository = TrackRepository(InjectionStub.realm)
        InjectionStub.photoUtils = PhotoUtils(this.getExternalFilesDir(PHOTOS_DIR))
    }

    private fun getRealmConfiguration() =
        RealmConfiguration.Builder()
            .initialData { realm -> realm.insertOrUpdate(skinQualityTrackType) }
            .deleteRealmIfMigrationNeeded()
            .build()

    companion object {
        const val PHOTOS_DIR = "photos"
        var skinQualityTrackType: TrackType = TrackType().apply {
            uuid = SKIN_QUALITY_TRACK_TYPE_UID
            name = SKIN_QUALITY_TRACK_NAME
            removable = false
        }
    }
}