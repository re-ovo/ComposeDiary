package me.rerere.composediary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xff35313a),
    primaryVariant = Color(0xff0f0914),
    secondary = Color(0xFF424242),
    secondaryVariant = Color(0xff1b1b1b)
)

val LightColorPalette = lightColors(
    primary = Color(0xffff4081),
    primaryVariant = Color(0xffc60055),
    secondary = Color(0xffff4081)
)

@Composable
fun ComposeDiaryTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}