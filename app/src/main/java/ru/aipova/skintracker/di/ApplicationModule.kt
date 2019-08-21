package ru.aipova.skintracker.di

import android.app.Application
import android.content.Context
import android.os.Environment
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.aipova.skintracker.utils.PhotoFileConstructor
import javax.inject.Singleton

@Module(includes = [ApplicationModule.Declarations::class])
class ApplicationModule {
    @Module
    interface Declarations {
        @Binds
        fun bindContext(application: Application): Context
    }

    @Provides
    @Singleton
    fun providePhotoFileConstructor(context: Context): PhotoFileConstructor {
        return PhotoFileConstructor(context.getExternalFilesDir(PHOTOS_DIR))
    }

    companion object {
        const val PHOTOS_DIR = "photos"
    }
}