package com.sarftec.worldproverbs.data.injection

import com.sarftec.worldproverbs.data.repository.DiskRepositoryImpl
import com.sarftec.worldproverbs.data.repository.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AbstractModule {

    @Singleton
    @Binds
    abstract fun repository(repository: DiskRepositoryImpl) : Repository
}