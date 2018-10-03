package ru.aipova.skintracker.ui.statistics;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.aipova.skintracker.di.ActivityScoped;
import ru.aipova.skintracker.di.FragmentScoped;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link StatisticsPresenter}.
 */
@Module
public abstract class StatisticsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract StatisticsFragment statisticsFragment();

    @ActivityScoped
    @Binds
    abstract StatisticsContract.Presenter statitsticsPresenter(StatisticsPresenter presenter);
}
