package ru.aipova.skintracker

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.realm.Realm
import io.realm.RealmConfiguration
import ru.aipova.skintracker.di.DaggerAppComponent

class SkinTrackerApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun onCreate() {
        super.onCreate()
        initRealm()
    }

    private fun initRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(getRealmConfiguration())
    }

    private fun getRealmConfiguration() =
        RealmConfiguration.Builder()
            .initialData(InitialData(this))
            .deleteRealmIfMigrationNeeded()
            .build()
}