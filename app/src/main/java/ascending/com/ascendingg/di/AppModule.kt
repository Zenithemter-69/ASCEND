package ascending.com.ascendingg.di

import android.content.Context
import ascending.com.ascendingg.data.database.AppDatabase
import ascending.com.ascendingg.data.repository.HabitRepository
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
}
