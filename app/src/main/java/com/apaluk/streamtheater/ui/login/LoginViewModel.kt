package com.apaluk.streamtheater.ui.login

import androidx.lifecycle.viewModelScope
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.resources.ResourcesManager
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.ui.common.util.UiState
import com.apaluk.streamtheater.ui.common.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginManager: LoginManager,
    private val resourcesManager: ResourcesManager
): BaseViewModel<LoginUiState, LoginEvent, LoginAction>() {

    override val initialState: LoginUiState = LoginUiState()

    init {
        // update screen ui state based on login state
        viewModelScope.launch {
            loginManager.loginState.collect { loginState ->
                emitUiState { it.copy(uiState = loginState.toUiState()) }
            }
        }
        // navigate to dashboard when logged in
        viewModelScope.launch {
            loginManager.loginState
                .filter { it == LoginManager.LoginState.LoggedIn }
                .collect { emitEvent(LoginEvent.NavigateToDashboard) }
        }
    }

    override fun handleAction(action: LoginAction) {
        when(action) {
            is LoginAction.UpdateUsername -> onUpdateUserName(action.userName)
            is LoginAction.UpdatePassword -> onUpdatePassword(action.password)
            LoginAction.LoginButtonClicked -> onLoginButtonClicked()
        }
    }

    private fun onUpdateUserName(userName: String) {
        emitUiState { it.copy(userName = userName) }
    }

    private fun onUpdatePassword(password: String) {
        emitUiState { it.copy(password = password) }
    }

    private fun onLoginButtonClicked() {
        viewModelScope.launch {
            val userName = uiState.value.userName
            val password = uiState.value.password
            if (userName.isBlank() || password.isBlank()) {
                emitUiState { it.copy(errorMessage = resourcesManager.getString(R.string.st_login_error_username_or_password_empty)) }
            } else {
                emitUiState { it.copy(isLoggingIn = true, errorMessage = null) }
                val loginResult = loginManager.tryLogin(userName, password)
                emitUiState { it.copy(isLoggingIn = false, errorMessage = loginResult.message) }
                if (loginResult is Resource.Success) {
                    emitEvent(LoginEvent.NavigateToDashboard)
                }
            }
        }
    }
}

data class LoginUiState(
    val uiState: UiState = UiState.Loading,
    val userName: String = "",
    val password: String = "",
    val isLoggingIn: Boolean = false,
    val errorMessage: String? = null
)

sealed class LoginAction {
    data class UpdateUsername(val userName: String): LoginAction()
    data class UpdatePassword(val password: String): LoginAction()
    object LoginButtonClicked: LoginAction()
}

sealed class LoginEvent {
    object NavigateToDashboard: LoginEvent()
}

private fun LoginManager.LoginState.toUiState(): UiState {
    return if (this == LoginManager.LoginState.Initializing || this == LoginManager.LoginState.LoggedIn)
        UiState.Loading
    else
        UiState.Content
}