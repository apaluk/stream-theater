@file:OptIn(ExperimentalMaterial3Api::class)

package com.apaluk.streamtheater.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apaluk.streamtheater.R
import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.ui.common.composable.EventHandler
import com.apaluk.streamtheater.ui.common.composable.TopAppBarAction
import com.apaluk.streamtheater.ui.common.util.PreviewDevices
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToMediaDetail: (String) -> Unit = {},
) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    TopAppBarAction(
                        icon = R.drawable.ic_search_24,
                        onClick = onNavigateToSearch
                    )
                }
            )
        },
        content = { padding ->
            DashboardContent(
                modifier = modifier.padding(padding),
                uiState = uiState,
                onDashboardAction = viewModel::onAction
            )
        }
    )
    EventHandler(viewModel.event) { event ->
        when (event) {
            is DashboardEvent.NavigateToMediaDetail -> onNavigateToMediaDetail(event.mediaId)
            is DashboardEvent.NavigateToSearch -> onNavigateToSearch()
        }
    }
}

@Composable
private fun DashboardContent(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState,
    onDashboardAction: (DashboardAction) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        uiState.continueWatchingMediaList?.let { continueWatchingMediaList ->
            if (continueWatchingMediaList.isNotEmpty()) {
                Text(
                    text = stringResourceSafe(id = R.string.st_dashboard_continue_watching),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    items(continueWatchingMediaList, key = { it.mediaId ?: "" }) {
                        DashboardMediaItem(
                            dashboardMedia = it,
                            onDashboardAction = onDashboardAction,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@PreviewDevices
@Composable
fun DashboardPreview() {
    StTheme {
        DashboardContent(
            uiState = DashboardUiState(
                continueWatchingMediaList = listOf(
                    DashboardMedia(
                        mediaId = "1",
                        title = "The Witcher",
                        imageUrl = "https://static.wikia.nocookie.net/witcher/images/4/4f/Tw3_journal_geralt.png/revision/latest?cb=20160525164048",
                    ),
                    DashboardMedia(
                        mediaId = "2",
                        title = "The Witcher",
                        imageUrl = "https://static.wikia.nocookie.net/witcher/images/4/4f/Tw3_journal_geralt.png/revision/latest?cb=20160525164048",
                    ),
                    DashboardMedia(
                        mediaId = "3",
                        title = "The Witcher",
                        imageUrl = "https://static.wikia.nocookie.net/witcher/images/4/4f/Tw3_journal_geralt.png/revision/latest?cb=20160525164048",
                    ),
                    DashboardMedia(
                        mediaId = "4",
                        title = "The Witcher",
                        imageUrl = "https://static.wikia.nocookie.net/witcher/images/4/4f/Tw3_journal_geralt.png/revision/latest?cb=20160525164048",
                    ),
                    DashboardMedia(
                        mediaId = "5",
                        title = "The Witcher",
                        imageUrl = "https://static.wikia.nocookie.net/witcher/images/4/4f/Tw3_journal_geralt.png/revision/latest?cb=20160525164048",
                    ),
                    DashboardMedia(
                        mediaId = "6",
                        title = "The Witcher",
                        imageUrl = "https://static.wikia.nocookie.net/witcher/images/4/4f/Tw3_journal_geralt.png/revision/latest?cb=20160525164048",
                    ),
                )
            ),
        )
    }
}

