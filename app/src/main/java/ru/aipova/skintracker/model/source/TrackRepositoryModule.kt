package ru.aipova.skintracker.model.source

import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Singleton

@Module
class TrackRepositoryModule {

    @Singleton
    @Provides
    fun provideRealm(): Realm {
        return Realm.getDefaultInstance()
    }
}