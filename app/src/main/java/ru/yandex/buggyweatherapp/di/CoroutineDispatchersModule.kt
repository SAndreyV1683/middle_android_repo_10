package ru.yandex.buggyweatherapp.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
class CoroutineDispatchersModule {
    @Provides
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}