package com.example.final_assignment_even_g28.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


data class ColorType(
    val unRead: Color,
    val read: Color,
    val old: Color
)
data class NotificationBellColor(
    val backgroundColor: ColorType,
    val borderColor: ColorType
)

val NotificationBellColorLightColorScheme = NotificationBellColor(
    backgroundColor = ColorType(
        unRead = NewNotificationBackgroundUnReadColor,
        read = NewNotificationBackgroundReadColor,
        old = OldNotificationBackgroundColor
    ),
    borderColor = ColorType (
        unRead = NewNotificationBorderUnReadColor,
        read = NewNotificationBorderReadColor,
        old = OldNotificationBorderColor
)
)

val NotificationBellColorDarkColorScheme = NotificationBellColor(
    backgroundColor = ColorType(
        unRead = NewNotificationBackgroundUnReadColorDark,
        read = NewNotificationBackgroundReadColorDark,
        old = OldNotificationBackgroundColorDark
    ),
    borderColor = ColorType (
        unRead = NewNotificationBorderUnReadColorDark,
        read = NewNotificationBorderReadColorDark,
        old = OldNotificationBorderColorDark
)
)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)


private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),

    )



val LocalNotificationBellColors = staticCompositionLocalOf { NotificationBellColorLightColorScheme }

@Composable
fun MadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val notificationBellColors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // For dynamic colors, you might want to use the light scheme or create dynamic variants
            if (darkTheme) NotificationBellColorDarkColorScheme else NotificationBellColorLightColorScheme
        }
        darkTheme -> NotificationBellColorDarkColorScheme
        else -> NotificationBellColorLightColorScheme
    }

    CompositionLocalProvider(
        LocalNotificationBellColors provides notificationBellColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}