package com.apaluk.streamtheater.core.login

import com.apaluk.streamtheater.core.util.Resource
import kotlinx.coroutines.flow.StateFlow

interface LoginManager {

    enum class LoginState {
        Initializing, LoggingIn, LoggedIn, LoggedOut
    }

    val loginState: StateFlow<LoginState>
    val loginError: StateFlow<String?>

    suspend fun tryLogin(username: String, password: String): Resource<Unit>
    suspend fun logout()
    suspend fun getWebShareToken(): String
}