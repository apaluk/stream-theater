@file:OptIn(ExperimentalFoundationApi::class, ExperimentalUnitApi::class)

package com.apaluk.streamtheater.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.apaluk.streamtheater.core.util.Constants
import com.apaluk.streamtheater.domain.model.dashboard.DashboardMedia
import com.apaluk.streamtheater.domain.model.dashboard.util.isEmpty
import com.apaluk.streamtheater.ui.common.composable.FullScreenLoader
import com.apaluk.streamtheater.ui.common.composable.StOutlinedButton
import com.apaluk.streamtheater.ui.common.composable.StOutlinedButtonStyle
import com.apaluk.streamtheater.ui.common.util.stringResourceSafe
import com.apaluk.streamtheater.ui.theme.StTheme
import com.apaluk.streamtheater.R

@Composable
fun DashboardMediaItem(
    dashboardMedia: DashboardMedia,
    modifier: Modifier = Modifier,
    onDashboardAction: (DashboardAction) -> Unit = {},
) {
    var isLongClicked by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .aspectRatio(1.618f)
            .combinedClickable(
                onClick = { onDashboardAction(DashboardAction.GoToMediaDetail(dashboardMedia)) },
                onLongClick = { isLongClicked = true },
            )
    ) {
        if(dashboardMedia.isEmpty) {
            FullScreenLoader()
        }
        else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        dashboardMedia.imageUrl?.let { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(durationMillis = Constants.SHORT_ANIM_DURATION)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(76.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.surface
                                        )
                                    )
                                )
                                .align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = dashboardMedia.title.orEmpty(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterStart),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                    }
                }
                this@Card.AnimatedVisibility(
                    visible = isLongClicked,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    DashboardMediaItemLongClickedLayer(
                        modifier = Modifier.fillMaxSize(),
                        onDiscard = { isLongClicked = false },
                        onRemoveFromList = { onDashboardAction(DashboardAction.RemoveFromList(dashboardMedia)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardMediaItemLongClickedLayer(
    modifier: Modifier = Modifier,
    onDiscard: () -> Unit = {},
    onRemoveFromList: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResourceSafe(id = R.string.st_dashboard_remove_media_from_history).uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                letterSpacing = TextUnit(1.4f, TextUnitType.Sp),
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                StOutlinedButton(
                    text = stringResourceSafe(id = R.string.st_no),
                    onClick = onDiscard,
                    style = StOutlinedButtonStyle.Light
                )
                Spacer(modifier = Modifier.width(16.dp))
                StOutlinedButton(
                    text = stringResourceSafe(id = R.string.st_yes),
                    onClick = onRemoveFromList,
                    style = StOutlinedButtonStyle.Highlighted
                )
            }
        }
    }
}

@Preview
@Composable
fun DashboardMediaItemPreview() {
    StTheme {
        DashboardMediaItem(
            dashboardMedia = DashboardMedia(
                mediaId = "1",
                title = "Title",
                imageUrl = "https://picsum.photos/200/300",
                duration = 123456,
                progressSeconds = 12345
            ),
        )
    }
}