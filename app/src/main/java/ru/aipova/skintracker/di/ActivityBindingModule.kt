package ru.aipova.skintracker.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.aipova.skintracker.ui.statistics.StatisticsActivity
import ru.aipova.skintracker.ui.statistics.StatisticsModule

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [StatisticsModule::class])
    abstract fun statisticsActivity(): StatisticsActivity
}