package com.certn.mobile.di

import com.certn.mobile.data.repository.CertnIDRepositoryImpl
import com.certn.mobile.domain.repository.CertnIDRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindCertnIDRepository(certnIDRepositoryImpl: CertnIDRepositoryImpl): CertnIDRepository

}
