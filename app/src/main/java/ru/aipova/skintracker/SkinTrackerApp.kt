package ru.aipova.skintracker

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aipova.skintracker.di.DaggerAppComponent
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_COUNT_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.PIMPLES_PICKING_TRACK_TYPE_UID
import ru.aipova.skintracker.model.TrackType.Companion.SKIN_QUALITY_TRACK_TYPE_UID
import ru.aipova.skintracker.model.ValueType
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackTypeRepository
import ru.aipova.skintracker.utils.PhotoFileConstructor

class SkinTrackerApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Realm.setDefaultConfiguration(getRealmConfiguration())

        InjectionStub.realm = Realm.getDefaultInstance()
        InjectionStub.trackTypeRepository = TrackTypeRepository(InjectionStub.realm)
        InjectionStub.trackRepository = TrackRepository(InjectionStub.realm)
        InjectionStub.photoFileConstructor = PhotoFileConstructor(this.getExternalFilesDir(PHOTOS_DIR))
    }

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

    private val pimplesPickingTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = PIMPLES_PICKING_TRACK_TYPE_UID
                name = getString(R.string.track_type_name_pimples_picking)
                setValueTypeEnum(ValueType.BOOLEAN)
            }
        }

    private fun initialTrackTypes() =
        listOf(skinQualityTrackType, pimplesCountTrackType, pimplesPickingTrackType)

    private fun getRealmConfiguration() =
        RealmConfiguration.Builder()
            .initialData { realm -> realm.insertOrUpdate(initialTrackTypes()) }
            .deleteRealmIfMigrationNeeded()
            .build()

    companion object {
        const val PHOTOS_DIR = "photos"

    }
}