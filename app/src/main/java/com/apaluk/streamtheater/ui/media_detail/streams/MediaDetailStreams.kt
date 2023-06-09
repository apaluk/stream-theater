package com.apaluk.streamtheater.ui.media_detail.streams

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apaluk.streamtheater.domain.model.media.DUMMY_MEDIA_STREAMS
import com.apaluk.streamtheater.domain.model.media.MediaStream
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.media_detail.StreamsUiState
import com.apaluk.streamtheater.ui.media_detail.util.selectedIndex
import com.apaluk.streamtheater.ui.theme.StTheme
import com.apaluk.streamtheater.R

@Composable
fun MediaDetailStreams(
    streamsUiState: StreamsUiState,
    modifier: Modifier = Modifier,
    onStreamSelected: (MediaStream) -> Unit = {},
) {
    val listState = rememberLazyListState()
    Card(
        modifier = modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResourceSafe(id = R.string.st_media_select_stream),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            LazyColumn(
                modifier = Modifier.padding(bottom = 8.dp),
                state = listState
            ) {
                itemsIndexed(streamsUiState.streams) { index, stream ->
                    MediaStreamChip(
                        mediaStream = stream,
                        modifier = modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .fillMaxWidth(),
                        onClick = { onStreamSelected(stream) },
                        isSelected = stream.ident == streamsUiState.selectedStreamId
                    )
                    if(index == streamsUiState.streams.lastIndex) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
    LaunchedEffect(streamsUiState.selectedIndex) {
        streamsUiState.selectedIndex?.let {
            listState.scrollToItem(it)
        }
    }
}

@Preview
@Composable
fun MediaDetailStreamsPreview() {
    StTheme {
        MediaDetailStreams(streamsUiState = StreamsUiState(DUMMY_MEDIA_STREAMS))
    }
}