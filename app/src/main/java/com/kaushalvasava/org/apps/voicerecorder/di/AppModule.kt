package com.kaushalvasava.org.apps.voicerecorder.di

import android.app.Application
import androidx.room.Room
import com.kaushalvasava.org.apps.voicerecorder.database.AppDatabase
import com.kaushalvasava.org.apps.voicerecorder.repo.RecorderRepo
import com.kaushalvasava.org.apps.voicerecorder.repo.RecorderRepoImpl
import com.kaushalvasava.org.apps.voicerecorder.utils.AppConstants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: AppDatabase): RecorderRepo {
        return RecorderRepoImpl(db.dao)
    }

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope