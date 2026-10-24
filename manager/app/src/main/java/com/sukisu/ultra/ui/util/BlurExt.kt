package com.sukisu.ultra.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.drawBackdrop
import top.yukonga.miuix.kmp.blur.effect
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.shader.isRenderEffectSupported
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun rememberBlurBackdrop(
    enableBlur: Boolean,
    surfaceColor: Color? = null,
): LayerBackdrop? {
    if (!enableBlur || !isRenderEffectSupported()) return null
    val resolvedSurfaceColor = surfaceColor ?: MiuixTheme.colorScheme.surface
    return rememberLayerBackdrop {
        drawRect(resolvedSurfaceColor)
        drawContent()
    }
}

@Composable
fun BlurredBar(
    backdrop: LayerBackdrop?,
    blurActive: Boolean = true,
    blurRadius: Float = 25f,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = if (blurActive && backdrop != null) {
            Modifier.textureBlur(
                backdrop = backdrop,
                shape = RectangleShape,
                blurRadius = blurRadius,
                colors = BlurColors(
                    blendColors = listOf(
                        BlendColorEntry(color = MiuixTheme.colorScheme.surface.copy(0.87f)),
                    ),
                ),
            )
        } else {
            Modifier
        },
    ) {
        content()
    }
}

@Composable
fun GaussianBlurredBar(
    backdrop: LayerBackdrop?,
    blurRadius: Dp,
    surfaceColor: Color,
    shape: Shape = RectangleShape,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val blurRadiusPx = with(density) { blurRadius.toPx() }
    Box(
        modifier = if (backdrop != null) {
            Modifier.drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    // Use Android's native GPU Gaussian blur instead of re-sampling a
                    // downscaled backdrop texture.
                    padding = maxOf(padding, blurRadiusPx)
                    effect(BlurEffect(blurRadiusPx, blurRadiusPx, TileMode.Clamp))
                },
                onDrawSurface = {
                    drawRect(surfaceColor.copy(alpha = 0.45f))
                },
            )
        } else {
            Modifier
        },
    ) {
        content()
    }
}
