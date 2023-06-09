package com.apaluk.streamtheater.ui.login

import com.apaluk.streamtheater.core.testing.LoginManagerFake
import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginManager: LoginManager = LoginManagerFake()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(loginManager)
    }

    @Test
    fun `setting username and password updates ui state`() = runTest {
        assertThat(viewModel.uiState.value.userName).isEmpty()
        assertThat(viewModel.uiState.value.password).isEmpty()
        viewModel.onLoginScreenAction(LoginScreenAction.UpdateUsername("user"))
        viewModel.onLoginScreenAction(LoginScreenAction.UpdatePassword("pass"))
        assertThat(viewModel.uiState.value.userName).isEqualTo("user")
        assertThat(viewModel.uiState.value.password).isEqualTo("pass")
    }

    @Test
    fun `successful login sets loggedIn flag`() = runTest {
        assertThat(viewModel.uiState.value.loggedIn).isFalse()
        viewModel.onLoginScreenAction(LoginScreenAction.UpdateUsername("user"))
        viewModel.onLoginScreenAction(LoginScreenAction.UpdatePassword("pass"))
        viewModel.onLoginScreenAction(LoginScreenAction.TriggerLogin)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.loggedIn).isTrue()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
        viewModel.onLoginScreenAction(LoginScreenAction.OnLoggedIn)
        assertThat(viewModel.uiState.value.loggedIn).isFalse()
    }

    @Test
    fun `unsuccessful login sets error message`() = runTest {
        assertThat(viewModel.uiState.value.loggedIn).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isNull()
        viewModel.onLoginScreenAction(LoginScreenAction.UpdateUsername("wrong"))
        viewModel.onLoginScreenAction(LoginScreenAction.UpdatePassword("pass"))
        viewModel.onLoginScreenAction(LoginScreenAction.TriggerLogin)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.loggedIn).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isNotEmpty()
    }
}