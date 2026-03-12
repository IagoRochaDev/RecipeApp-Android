package com.devrochaiago.recipeapp.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Modificador customizado que adiciona um efeito de "esqueleto a carregar" (Shimmer)
 */
fun Modifier.shimmerEffect(): Modifier = composed {

    val transition = rememberInfiniteTransition(label = "shimmer_transition")

    val translateAnimation by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 3000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 500f, translateAnimation + 500f)
    )

    this.then(Modifier.background(brush))
}