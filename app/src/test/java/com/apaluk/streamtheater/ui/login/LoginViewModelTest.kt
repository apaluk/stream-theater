package com.apaluk.streamtheater.ui.login

import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.testing.LoginManagerFake
import com.apaluk.streamtheater.core.testing.ResourcesManagerDummy
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        viewModel = LoginViewModel(loginManager, ResourcesManagerDummy())
    }

    @Test
    fun `setting username and password updates ui state`() = runTest {
        assertThat(viewModel.uiState.value.userName).isEmpty()
        assertThat(viewModel.uiState.value.password).isEmpty()
        viewModel.onAction(LoginAction.UpdateUsername("user"))
        viewModel.onAction(LoginAction.UpdatePassword("pass"))
        assertThat(viewModel.uiState.value.userName).isEqualTo("user")
        assertThat(viewModel.uiState.value.password).isEqualTo("pass")
    }


    // TODO
    @Test
    fun `successful login sets loggedIn flag`() = runTest {
//        assertThat(viewModel.uiState.value.loggedIn).isFalse()
//        viewModel.onAction(LoginAction.UpdateUsername("user"))
//        viewModel.onAction(LoginAction.UpdatePassword("pass"))
//        viewModel.onAction(LoginAction.LoginButtonClicked)
//        advanceUntilIdle()
//        assertThat(viewModel.uiState.value.loggedIn).isTrue()
//        assertThat(viewModel.uiState.value.errorMessage).isNull()
//        viewModel.onAction(LoginAction.OnLoggedIn)
//        assertThat(viewModel.uiState.value.loggedIn).isFalse()
    }

    // TODO
    @Test
    fun `unsuccessful login sets error message`() = runTest {
//        assertThat(viewModel.uiState.value.loggedIn).isFalse()
//        assertThat(viewModel.uiState.value.errorMessage).isNull()
//        viewModel.onAction(LoginAction.UpdateUsername("wrong"))
//        viewModel.onAction(LoginAction.UpdatePassword("pass"))
//        viewModel.onAction(LoginAction.LoginButtonClicked)
//        advanceUntilIdle()
//        assertThat(viewModel.uiState.value.loggedIn).isFalse()
//        assertThat(viewModel.uiState.value.errorMessage).isNotEmpty()
    }
}