package ru.aipova.skintracker.ui.trackpager

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.aipova.skintracker.di.ActivityScoped
import ru.aipova.skintracker.di.FragmentScoped
import ru.aipova.skintracker.ui.trackpager.track.TrackContract
import ru.aipova.skintracker.ui.trackpager.track.TrackFragment
import ru.aipova.skintracker.ui.trackpager.track.TrackPresenter

@Module
abstract class TrackPagerModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun trackFragment(): TrackFragment

    @ActivityScoped
    @Binds
    abstract fun trackPresenter(presenter: TrackPresenter): TrackContract.Presenter

    @ActivityScoped
    @Binds
    abstract fun trackPagerPresenter(presenter: TrackPagerPresenter): TrackPagerContract.Presenter
}
