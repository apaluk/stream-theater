package com.apaluk.streamtheater.di

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.login.LoginManagerImpl
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.core.resources.ResourcesManagerImpl
import com.apaluk.streamtheater.core.settings.AppSettings
import com.apaluk.streamtheater.data.webshare.WebShareRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @ApplicationScope
    @Provides
    fun provideApplicationScope(): CoroutineScope {
        return ProcessLifecycleOwner.get().lifecycleScope
    }

    @Provides
    @Singleton
    fun provideLoginManager(
        @ApplicationScope scope: CoroutineScope,
        appSettings: AppSettings,
        webShareRepository: WebShareRepository
    ): LoginManager {
        return LoginManagerImpl(scope, appSettings, webShareRepository)
    }

    @Provides
    @Singleton
    fun provideResourcesManager(@ApplicationContext applicationContext: Context): ResourcesManager {
        return ResourcesManagerImpl(applicationContext)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope