package com.ht.websocket.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = MyColor.Blue,
            onPrimary = MyColor.White,

            secondary = Color(0xFFFF6D4D),
            onSecondary = MyColor.Black,

            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E),
            onSurface = MyColor.Grey2,

            tertiary = Color(0xFF2C2C2C),
            onTertiary = MyColor.Grey2
        )
    } else {
        lightColorScheme(
            primary = MyColor.Blue,
            onPrimary = MyColor.White,

            secondary = Color(0xFFFF6D4D),
            onSecondary = MyColor.White,

            background = MyColor.White,
            surface = MyColor.White,
            onSurface = MyColor.Black,

            tertiary = MyColor.Grey0,
            onTertiary = MyColor.Black
        )
    }

    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

object MyColor {
    val Black = Color.Black
    val White: Color = Color.White
    val Blue = Color(0xFF2D9BF0)
    val Grey0 = Color(0xFFe9eaeb)
    val Grey1 = Color(0xFFf5f5f5)
    val Grey2 = Color(0xFFd5d7da)


}
