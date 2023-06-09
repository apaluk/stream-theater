package com.apaluk.streamtheater.core.login

import com.apaluk.streamtheater.core.login.LoginManager.LoginState.*
import com.apaluk.streamtheater.core.settings.AppSettings
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.hashing.md5Crypt
import com.apaluk.streamtheater.core.util.hashing.sha1
import com.apaluk.streamtheater.core.util.requireNotNullOrEmpty
import com.apaluk.streamtheater.data.webshare.WebShareRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginManagerImpl(
    scope: CoroutineScope,
    private val appSettings: AppSettings,
    private val webShareRepository: WebShareRepository
): LoginManager {
    
    private val _loginState = MutableStateFlow(Initializing)
    override val loginState = _loginState.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    override val loginError = _loginError.asStateFlow()

    private val webshareToken = MutableStateFlow("")

    init {
        scope.launch {
            val storedToken = appSettings.webshareToken.first()
            if(storedToken.isNotEmpty())
                _loginState.value = LoggedIn

            webshareToken.value = storedToken
            ensureFreshToken()
            _loginState.value =
                if(webshareToken.value.isNotEmpty()) LoggedIn else LoggedOut
        }
    }

    override suspend fun tryLogin(username: String, password: String): Resource<Unit> {
        logout()
        _loginState.value = LoggingIn
        val loginResult = loginInternal(username, password)
        _loginState.value = if(loginResult is Resource.Success) LoggedIn else LoggedOut
        return loginResult
    }

    override suspend fun logout() {
        _loginState.value = LoggedOut
        webshareToken.value = ""
        appSettings.setWebshareToken("")
        appSettings.setUsername("")
        appSettings.setPasswordDigest("")
    }

    override suspend fun getWebShareToken(): String {
        ensureFreshToken()
        // asking token when it's empty or null would be error in app flow.
        return requireNotNullOrEmpty(webshareToken.value)
    }

    private suspend fun loginInternal(username: String, password: String): Resource<Unit> {
        val salt = webShareRepository.getSalt(username).last().data ?: run {
            // seems that getSalt returns error for invalid username
            Timber.e("Failed to get salt.")
            return Resource.Error(message = "Invalid username")
        }
        val passwordDigest = password.md5Crypt(salt).sha1()
        val loginResource = webShareRepository.login(username, passwordDigest).last()
        when(loginResource) {
            is Resource.Success -> {
                val token = requireNotNull(loginResource.data)
                webshareToken.value = token
                appSettings.setWebshareToken(token)
                appSettings.setUsername(username)
                appSettings.setPasswordDigest(passwordDigest)
                return Resource.Success(Unit)
            }
            else -> {}
        }
        return Resource.Error(message = loginResource.message ?: "Unknown error. Please try again later.")
    }

    private suspend fun ensureFreshToken() {
        val currentTokenCreated = appSettings.webshareTokenCreated.first()
        val now = System.currentTimeMillis()

        if(now - currentTokenCreated > TOKEN_VALIDITY_MILLIS) {
            val username = appSettings.username.first()
            val passwordDigest = appSettings.passwordDigest.first()
            if(username.isEmpty() || passwordDigest.isEmpty()) {
                logout()
                return
            }

            when(val loginResult = webShareRepository.login(username, passwordDigest).last()) {
                is Resource.Success -> {
                    appSettings.setWebshareToken(requireNotNull(loginResult.data))
                    webshareToken.value = loginResult.data
                }
                is Resource.Error -> {
                    Timber.w("Failed to update WebShare token!")
                }
                is Resource.Loading -> {}   // ignore
            }
        }
    }

    companion object {
        private const val TOKEN_VALIDITY_MILLIS = 24 * 3600 * 1000L
    }
}