package ru.aipova.skintracker.ui.trackvalues

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.aipova.skintracker.di.ActivityScoped
import ru.aipova.skintracker.di.FragmentScoped

@Module
abstract class TrackValuesModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun trackValuesFragment(): TrackValuesFragment

    @ActivityScoped
    @Binds
    abstract fun trackValuesPresenter(presenter: TrackValuesPresenter): TrackValuesContract.Presenter
}
