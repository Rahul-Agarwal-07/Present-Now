package com.example.presentationmaker

import androidx.compose.animation.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentationmaker.ui.theme.Purple40
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.example.presentationmaker.ui.theme.Purple80
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimation(
    circleSize : Dp = 25.dp,
    circleColor : Color = Purple40,
    spaceBetween : Dp = 10.dp,
    travelDist : Dp = 20.dp
)
{
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
    )

    circles.forEachIndexed{
        index, animatable ->
        
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)

            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 using LinearOutSlowInEasing
                        1.0f at 300 using LinearOutSlowInEasing
                        0.0f at 600 using LinearOutSlowInEasing
                        0.0f at 1200 using LinearOutSlowInEasing
                    },

                    repeatMode = RepeatMode.Restart
                )
            )
        }


    }

    val circlesValues = circles.map { it.value }
    val distance = with(LocalDensity.current) { travelDist.toPx() }
    val lastCircle = circles.size - 1

    Row()
    {
        circlesValues.forEachIndexed {
            index,
            value ->

            Box(
                modifier = Modifier
                    .size(circleSize)
                    .graphicsLayer {
                        translationY = -value * distance
                    }
                    .background(
                        color = circleColor,
                        shape = CircleShape
                    )
            )

            if(index != lastCircle)
            {
                Spacer(modifier = Modifier.width(spaceBetween))
            }
        }
    }
}

@Preview
@Composable
fun LoadingAnimPreview()
{
    LoadingAnimation()
}