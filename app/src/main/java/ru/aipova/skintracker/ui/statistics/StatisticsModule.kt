package ru.aipova.skintracker.ui.statistics

import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.aipova.skintracker.di.ActivityScoped
import ru.aipova.skintracker.di.FragmentScoped

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * [StatisticsPresenter].
 */
@Module
abstract class StatisticsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun statisticsFragment(): StatisticsFragment

    @ActivityScoped
    @Binds
    abstract fun statitsticsPresenter(presenter: StatisticsPresenter): StatisticsContract.Presenter
}
