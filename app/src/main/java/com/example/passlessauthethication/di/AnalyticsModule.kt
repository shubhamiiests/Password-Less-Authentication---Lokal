package com.example.passlessauthethication.di


import com.example.passlessauthethication.presentation.analytics.AnalyticsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsLogger(): AnalyticsLogger {
        return AnalyticsLogger()
    }
}