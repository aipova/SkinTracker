package ru.aipova.skintracker

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
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

    private fun getRealmConfiguration() =
        RealmConfiguration.Builder()
            .initialData(InitialData(this))
            .deleteRealmIfMigrationNeeded()
            .build()

    companion object {
        const val PHOTOS_DIR = "photos"
    }
}