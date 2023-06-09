package com.apaluk.streamtheater.core.settings

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettings @Inject constructor(
    private val app: Application
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

    val username: Flow<String> = app.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.username] ?: ""
    }

    suspend fun setUsername(username: String) {
        app.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKeys.username] = username
        }
    }

    val passwordDigest: Flow<String> = app.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.passwordDigest] ?: ""
    }

    suspend fun setPasswordDigest(password: String) {
        app.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKeys.passwordDigest] = password
        }
    }

    val webshareToken: Flow<String> = app.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.webshareToken] ?: ""
    }

    val webshareTokenCreated: Flow<Long> = app.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.webshareTokenCreated] ?: 0L
    }

    suspend fun setWebshareToken(token: String) {
        app.dataStore.edit { mutablePreferences ->
            mutablePreferences[PreferenceKeys.webshareToken] = token
            mutablePreferences[PreferenceKeys.webshareTokenCreated] =
                if(token.isNotEmpty()) System.currentTimeMillis() else 0L
        }
    }

    private object PreferenceKeys {
        val username = stringPreferencesKey("username")
        val passwordDigest = stringPreferencesKey("password")
        val webshareToken = stringPreferencesKey("webshareToken")
        val webshareTokenCreated = longPreferencesKey("webshareTokenCreated")
    }

    companion object {
        private const val DATASTORE_NAME = "AppSettings"
    }
}