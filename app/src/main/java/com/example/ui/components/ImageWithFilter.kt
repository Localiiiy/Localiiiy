package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.FilterPreset

@Composable
fun ImageWithFilter(
    mediaUrl: String,
    filterName: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null
) {
    val colorMatrix = when (filterName) {
        "Moon" -> ColorMatrix().apply {
            setToSaturation(0.0f)
        }
        "Clarendon" -> ColorMatrix().apply {
            setToSaturation(1.25f)
        }
        "Juno" -> ColorMatrix().apply {
            setToSaturation(1.35f)
        }
        "Valencia" -> ColorMatrix().apply {
            setToSaturation(0.9f)
        }
        "Neon" -> ColorMatrix().apply {
            setToSaturation(1.4f)
        }
        "Vintage" -> ColorMatrix().apply {
            setToSaturation(0.85f)
        }
        else -> null
    }

    val overlayColor = when (filterName) {
        "Clarendon" -> Color(0x1A0077FF)
        "Juno" -> Color(0x1AFF5500)
        "Valencia" -> Color(0x22FFAA33)
        "Vintage" -> Color(0x228B5A2B)
        "Neon" -> Color(0x1AFF007F)
        else -> null
    }

    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(mediaUrl)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            colorFilter = colorMatrix?.let { ColorFilter.colorMatrix(it) },
            modifier = Modifier.fillMaxSize()
        )

        if (overlayColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(overlayColor)
            )
        }
    }
}
