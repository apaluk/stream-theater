package com.apaluk.streamtheater.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.apaluk.streamtheater.R

@Composable
fun SearchHistoryList(
    searchHistoryList: List<String>,
    modifier: Modifier = Modifier,
    onItemSelected: (String) -> Unit = {},
    onTriggerSearch: () -> Unit = {},
    onDeleteItem: (String) -> Unit = {},
) {
    LazyColumn(modifier = modifier) {
        items(searchHistoryList) {
            SearchHistoryItem(
                text = it,
                onItemSelected = { text ->
                    onItemSelected(text)
                    onTriggerSearch()
                },
                onItemAppend = { text ->
                    onItemSelected(text)
                },
                onDeleteItem = { text ->
                    onDeleteItem(text)
                }
            )
        }
    }
}

@Composable
fun SearchHistoryItem(
    text: String,
    onItemSelected: (String) -> Unit = {},
    onItemAppend: (String) -> Unit = {},
    onDeleteItem: (String) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemSelected("${text.trim()} ") }
            .padding(horizontal = 64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .alpha(0.7f)
                .weight(1f),
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .size(48.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable { onDeleteItem(text) },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_delete_24),
                contentDescription = "Delete",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
            )
        }
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .size(48.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable { onItemAppend("${text.trim()} ") },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_outward_top_left_24),
                contentDescription = "Delete",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline),
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            )
        }
    }
}