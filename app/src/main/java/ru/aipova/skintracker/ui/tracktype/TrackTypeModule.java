package ru.aipova.skintracker.ui.tracktype;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.aipova.skintracker.di.ActivityScoped;
import ru.aipova.skintracker.di.FragmentScoped;

@Module
public abstract class TrackTypeModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract TrackTypeFragment trackTypeFragment();

    @ActivityScoped
    @Binds
    abstract TrackTypeContract.Presenter trackTypePresenter(TrackTypePresenter presenter);
}
