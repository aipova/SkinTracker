package ru.aipova.skintracker

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_COUNT_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_PICKING_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.SUGAR_TRACK_TYPE_UID
import ru.aipova.skintracker.model.ValueType
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackTypeRepository
import ru.aipova.skintracker.utils.PhotoFileConstructor

class SkinTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(getRealmConfiguration())

        InjectionStub.realm = Realm.getDefaultInstance()
        InjectionStub.trackTypeRepository = TrackTypeRepository(InjectionStub.realm)
        InjectionStub.trackRepository = TrackRepository(InjectionStub.realm)
        InjectionStub.photoFileConstructor = PhotoFileConstructor(this.getExternalFilesDir(PHOTOS_DIR))
    }

    private fun initialTrackTypes() =
        listOf(skinQualityTrackType, pimplesCountTrackType, sugarTrackType, pimplesPickingTrackType)

    private fun getRealmConfiguration() =
        RealmConfiguration.Builder()
            .initialData { realm -> realm.insertOrUpdate(initialTrackTypes()) }
            .deleteRealmIfMigrationNeeded()
            .build()

    // TODO move out predefined examples
    private val  skinQualityTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = SKIN_QUALITY_TRACK_TYPE_UID
                name = getString(R.string.track_type_name_skin_quality)
                setValueTypeEnum(ValueType.SEEK)
            }
        }

    private val  pimplesCountTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = PIMPLES_COUNT_TRACK_TYPE_UID
                name = getString(R.string.track_type_name_pimples_amount)
                setValueTypeEnum(ValueType.AMOUNT)
            }
        }

    private val  sugarTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = SUGAR_TRACK_TYPE_UID
                name = getString(R.string.track_type_name_sugar)
                setValueTypeEnum(ValueType.AMOUNT)
            }
        }

    private val pimplesPickingTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = PIMPLES_PICKING_TRACK_TYPE_UID
                name = getString(R.string.track_type_name_pimples_picking)
                setValueTypeEnum(ValueType.BOOLEAN)
            }
        }

    companion object {
        const val PHOTOS_DIR = "photos"
    }
}