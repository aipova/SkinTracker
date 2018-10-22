package ru.aipova.skintracker.ui.tracktype

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.aipova.skintracker.di.FragmentScoped

@Module
abstract class TrackTypeModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun trackTypeFragment(): TrackTypeFragment

    @FragmentScoped
    @Binds
    abstract fun trackTypePresenter(presenter: TrackTypePresenter): TrackTypeContract.Presenter
}
