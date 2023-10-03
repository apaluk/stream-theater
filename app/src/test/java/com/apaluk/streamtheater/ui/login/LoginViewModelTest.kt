package com.apaluk.streamtheater.ui.login

import app.cash.turbine.test
import com.apaluk.streamtheater.core.login.LoginManager
import com.apaluk.streamtheater.core.util.MainDispatcherRule
import com.apaluk.streamtheater.core.util.Resource
import com.apaluk.streamtheater.core.util.mockkResourcesManager
import com.google.common.truth.Truth.assertThat
import io.mockk.Called
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var loginManager: LoginManager

    private val loginStateFlow = MutableStateFlow(LoginManager.LoginState.LoggedOut)

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { loginManager.tryLogin(any(), any()) } returns Resource.Success(Unit)
        every { loginManager.loginState } returns loginStateFlow
    }

    @Test
    fun `setting username and password, updates username and password`() = runTest {
        createViewModel()
        assertThat(viewModel.uiState.value.userName).isEmpty()
        assertThat(viewModel.uiState.value.password).isEmpty()
        viewModel.onAction(LoginAction.UpdateUsername("user"))
        viewModel.onAction(LoginAction.UpdatePassword("pass"))
        assertThat(viewModel.uiState.value.userName).isEqualTo("user")
        assertThat(viewModel.uiState.value.password).isEqualTo("pass")
    }

    @Test
    fun `given empty name and password, when clicking login button, login is not triggered and error message is set`() = runTest {
        createViewModel()
        viewModel.onAction(LoginAction.LoginButtonClicked)
        coVerify { loginManager.tryLogin(any(), any()) wasNot Called }
        assertThat(viewModel.uiState.value.errorMessage).isNotEmpty()
    }

    @Test
    fun `given username and password, when clicking login button, login is triggered`() = runTest {
        createViewModel()
        viewModel.onAction(LoginAction.UpdateUsername("user"))
        viewModel.onAction(LoginAction.UpdatePassword("pass"))
        viewModel.onAction(LoginAction.LoginButtonClicked)
        coVerify { loginManager.tryLogin(any(), any()) }
    }

    @Test
    fun `when loginState changed to LoggedIn, redirect to dashboard`() = runTest {
        createViewModel()
        loginStateFlow.value = LoginManager.LoginState.LoggedIn
        viewModel.event.test {
            assertThat(awaitItem()).isEqualTo(LoginEvent.NavigateToDashboard)
        }
    }

    private fun createViewModel() {
        viewModel = LoginViewModel(loginManager, mockkResourcesManager())
    }
}