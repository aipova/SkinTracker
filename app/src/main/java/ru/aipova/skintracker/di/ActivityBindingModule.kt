package ru.aipova.skintracker.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.aipova.skintracker.ui.statistics.StatisticsActivity
import ru.aipova.skintracker.ui.statistics.StatisticsModule
import ru.aipova.skintracker.ui.trackpager.TrackPagerActivity
import ru.aipova.skintracker.ui.trackpager.TrackPagerModule
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity
import ru.aipova.skintracker.ui.tracktype.TrackTypeModule

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [StatisticsModule::class])
    abstract fun statisticsActivity(): StatisticsActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [TrackTypeModule::class])
    abstract fun trackTypeActivity(): TrackTypeActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [TrackPagerModule::class])
    abstract fun trackPagerActivity(): TrackPagerActivity
}