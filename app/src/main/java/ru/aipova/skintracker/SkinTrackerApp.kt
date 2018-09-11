package ru.aipova.skintracker

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_COUNT_TRACK_NAME
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_COUNT_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_PICKING_TRACK_NAME
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_PICKING_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_NAME
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_TYPE_UID
import ru.aipova.skintracker.model.ValueType
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
            .initialData { realm -> realm.insertOrUpdate(initialTrackTypes) }
            .deleteRealmIfMigrationNeeded()
            .build()

    companion object {
        const val PHOTOS_DIR = "photos"
        var skinQualityTrackType: TrackType = TrackType().apply {
            uuid = SKIN_QUALITY_TRACK_TYPE_UID
            name = SKIN_QUALITY_TRACK_NAME
            removable = false
            setValueTypeEnum(ValueType.SEEK)
        }
        var pimplesCountTrackType: TrackType = TrackType().apply {
            uuid = PIMPLES_COUNT_TRACK_TYPE_UID
            name = PIMPLES_COUNT_TRACK_NAME
            removable = false
            setValueTypeEnum(ValueType.AMOUNT)
        }
        var pimplesPickingTrackType: TrackType = TrackType().apply {
            uuid = PIMPLES_PICKING_TRACK_TYPE_UID
            name = PIMPLES_PICKING_TRACK_NAME
            removable = false
            setValueTypeEnum(ValueType.BOOLEAN)
        }
        var initialTrackTypes = listOf(skinQualityTrackType, pimplesCountTrackType, pimplesPickingTrackType)
    }
}