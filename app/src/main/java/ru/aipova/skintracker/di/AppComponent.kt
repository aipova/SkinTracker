package ru.aipova.skintracker.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.aipova.skintracker.SkinTrackerApp
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackRepositoryModule
import ru.aipova.skintracker.model.source.TrackTypeRepository
import javax.inject.Singleton

@Component(modules = [ApplicationModule::class,
    ActivityBindingModule::class,
    TrackRepositoryModule::class,
    AndroidSupportInjectionModule::class])
@Singleton
interface AppComponent : AndroidInjector<SkinTrackerApp> {

    fun getTrackRepository(): TrackRepository

    fun getTrackTypeRepository(): TrackTypeRepository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }
}