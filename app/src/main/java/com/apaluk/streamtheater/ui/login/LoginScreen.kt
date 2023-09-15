@file:OptIn(ExperimentalComposeUiApi::class)

package com.apaluk.streamtheater.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.ui.common.composable.StButton
import com.apaluk.streamtheater.ui.common.composable.TextFieldWithHeader
import com.apaluk.streamtheater.ui.common.composable.EventHandler
import com.apaluk.streamtheater.ui.common.composable.UiStateAnimator
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onNavigateToDashboard: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LoginScreenContent(
        uiState = uiState,
        onLoginScreenAction = viewModel::onAction,
        modifier = modifier
    )
    EventHandler(viewModel.event) { event ->
        when (event) {
            is LoginEvent.NavigateToDashboard -> onNavigateToDashboard()
        }
    }
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    onLoginScreenAction: (LoginAction) -> Unit = {}
) {
    UiStateAnimator(uiState = uiState.uiState) {
        LoginScreenForm(
            modifier = modifier,
            uiState = uiState,
            onLoginScreenAction = onLoginScreenAction
        )
    }
}

@Composable
fun LoginScreenForm(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    onLoginScreenAction: (LoginAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        val keyboardController = LocalSoftwareKeyboardController.current
        Column(
            modifier = modifier
                .widthIn(max = 500.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                text = stringResourceSafe(id = R.string.st_login_welcome),
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                text = stringResourceSafe(id = R.string.st_login_instructions),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextFieldWithHeader(
                modifier = Modifier.testTag("login:username"),
                header = stringResourceSafe(id = R.string.st_login_username),
                editText = uiState.userName,
                onTextChanged = {
                    onLoginScreenAction(LoginAction.UpdateUsername(it))
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = modifier.height(16.dp))
            TextFieldWithHeader(
                header = stringResourceSafe(id = R.string.st_login_password),
                editText = uiState.password,
                onTextChanged =  {
                    onLoginScreenAction(LoginAction.UpdatePassword(it))
                },
                modifier = Modifier.testTag("login:password"),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onLoginScreenAction(LoginAction.LoginButtonClicked)
                    }
                ),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StButton(
                    onClick = { onLoginScreenAction(LoginAction.LoginButtonClicked) },
                    text = stringResourceSafe(id = R.string.st_login_positive_button),
                    enabled = uiState.isLoggingIn.not(),
                )
                if(uiState.isLoggingIn) {
                    Spacer(modifier = Modifier.width(32.dp))
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .semantics {
                                contentDescription = "login:spinner"
                            }
                    )
                }
            }
            uiState.errorMessage?.let { errorMessage ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun BasicPreview() {
    StTheme {
        LoginScreenForm(
            modifier = Modifier,
            uiState = LoginUiState(
                userName = "apaluk",
                password = "nbusr123",
                isLoggingIn = false,
                errorMessage = "Something went wrong!"
            ),
            onLoginScreenAction = {}
        )
    }
}
