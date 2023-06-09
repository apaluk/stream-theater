package com.apaluk.streamtheater.core.testing

import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginManagerFake: LoginManager {

    private val _loginState = MutableStateFlow(LoginManager.LoginState.Initializing)
    override val loginState = _loginState.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    override val loginError = _loginError.asStateFlow()

    override suspend fun tryLogin(username: String, password: String): Resource<Unit> {
        return if(username == "wrong") {
            _loginState.value = LoginManager.LoginState.LoggedOut
            _loginError.value = "error"
            Resource.Error(message = "error")
        }
        else {
            _loginState.value = LoginManager.LoginState.LoggedIn
            _loginError.value = null
            Resource.Success(Unit)
        }
    }

    override suspend fun logout() {
        _loginState.value = LoginManager.LoginState.LoggedIn
        _loginError.value = null
    }

    override suspend fun getWebShareToken(): String = "token"
}