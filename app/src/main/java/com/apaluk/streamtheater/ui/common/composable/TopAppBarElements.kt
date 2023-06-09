package com.apaluk.streamtheater.ui.common.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TopAppBarAction(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .padding(8.dp)
            .size(48.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
            )

    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = { onBack() }
    ) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back",
        )
    }
}