package com.sarftec.worldproverbs.data.injection

import android.content.Context
import androidx.room.Room
import com.sarftec.worldproverbs.data.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StaticModule {

    @Singleton
    @Provides
    fun database(@ApplicationContext context: Context) : Database {
        return  Room.databaseBuilder(
            context,
            Database::class.java,
            "app_database"
        ).build()
    }
}