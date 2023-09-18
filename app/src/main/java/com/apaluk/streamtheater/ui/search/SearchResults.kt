package com.apaluk.streamtheater.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.domain.model.search.SearchResultItem
import com.apaluk.streamtheater.ui.common.composable.MediaTitle
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun SearchResults(
    results: List<SearchResultItem>,
    modifier: Modifier = Modifier,
    onResultClicked: (String) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(results) { result ->
            SearchResult(
                item = result,
                onClicked = { onResultClicked(result.id) }
            )
            Divider(
                modifier = Modifier.padding(horizontal = 6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun SearchResult(
    item: SearchResultItem,
    onClicked: () -> Unit,
) {
    val imgHeight = 180.dp
    val imgWidth = 120.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(imgHeight)
            .clickable { onClicked() }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(imgWidth)
                .height(imgHeight)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (item.imageUrl.isNullOrEmpty()) {
                Image(
                    modifier = Modifier
                        .padding(start = 4.dp),
                    painter = painterResource(id = R.drawable.ic_movie_64),
                    contentDescription = null
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(start = 4.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(durationMillis = Constants.SHORT_ANIM_DURATION)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            MediaTitle(
                title = item.title,
                originalTitle = item.originalTitle
            )
            Text(
                modifier = Modifier.padding(vertical = 16.dp),
                text = item.generalInfo,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.cast,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
fun SearchResultsPreview() {
    StTheme {
        SearchResult(
            item = SearchResultItem(
                id = "",
                title = "Pulp fiction",
                originalTitle = "Pulp fiction",
                generalInfo = "1994 ${Constants.CHAR_BULLET}  USA  ${Constants.CHAR_BULLET}  Crime, Drama",
                cast = "Bruce Willis, John Travolta, Samuel L. Jackson",
                imageUrl = null
            ),
            onClicked = {}
        )
    }
}