package ascending.com.ascendingg.di

import android.content.Context
import ascending.com.ascendingg.data.database.AppDatabase
import ascending.com.ascendingg.data.repository.AIRepository
import ascending.com.ascendingg.data.repository.HabitRepository
import ascending.com.ascendingg.data.tracking.LocationTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext application: Context): AppDatabase =
        AppDatabase.getInstance(application)

    @Provides
    @Singleton
    fun provideHabitRepository(database: AppDatabase): HabitRepository =
        HabitRepository(database)

    @Provides
    @Singleton
    fun provideAIRepository(@ApplicationContext application: Context): AIRepository =
        AIRepository(application)

    @Provides
    @Singleton
    fun provideLocationTracker(@ApplicationContext application: Context): LocationTracker =
        LocationTracker(application)
}