package com.apaluk.streamtheater.ui.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apaluk.streamtheater.ui.theme.StTheme

// TODO remove from common?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithHeader(
    modifier: Modifier = Modifier,
    header: String,
    editText: String,
    onTextChanged: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column {
        Text(
            modifier = Modifier.padding(8.dp),
            text = header,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        TextField(
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            value = editText,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor  = Color.Transparent,
                disabledIndicatorColor  = Color.Transparent,
            ),
            onValueChange = onTextChanged,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview
@Composable
fun TextWithHeaderPreview() {
    StTheme {
        TextFieldWithHeader(
            header = "Username",
            editText = "hello",
            onTextChanged = {}
        )
    }
}