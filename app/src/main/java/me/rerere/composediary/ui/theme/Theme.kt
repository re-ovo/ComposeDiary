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
    primary = Color.White.copy(),
    primaryVariant = Color(0xffcccccc),
    onPrimary = Color.Black.copy(),

    secondary = Color(0xffe91e63),
    secondaryVariant = Color(0xffb0003a),
    onSecondary = Color.White
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