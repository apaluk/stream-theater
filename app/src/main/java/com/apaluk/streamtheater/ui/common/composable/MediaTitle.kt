package com.apaluk.streamtheater.ui.common.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun MediaTitle(
    title: String,
    originalTitle: String?,
    modifier: Modifier = Modifier,
    titleTextStyle: TextStyle = MaterialTheme.typography.headlineMedium,
    originalTitleTextStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    showOriginalTitleIfSame: Boolean = false
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = title,
            style = titleTextStyle,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (originalTitle != null && (originalTitle != title || showOriginalTitleIfSame)) {
            Text(
                modifier = Modifier.padding(start = 6.dp).alignByBaseline(),
                text = "($originalTitle)",
                style = originalTitleTextStyle,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}