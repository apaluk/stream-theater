@file:OptIn(ExperimentalComposeUiApi::class)

package com.apaluk.streamtheater.ui.common.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController

/**
 * A state object that can be used to control the software keyboard visibility. Also sets focus
 * to the TextField bound via [focusRequester].
 */
class TextFieldKeyboardState constructor(
    val focusRequester: FocusRequester,
    private val keyboardController: SoftwareKeyboardController?
) {

    fun showKeyboard(show: Boolean) {
        if (show) focusRequester.requestFocus() else keyboardController?.hide()
    }
}

@Composable
fun rememberTextFieldKeyboardState(): TextFieldKeyboardState {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    return remember {
        TextFieldKeyboardState(focusRequester, softwareKeyboardController)
    }
}