package com.example.cycletracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun CycleTheme(
	useDarkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val colorScheme = when {
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
			val context = LocalContext.current
			if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}
		useDarkTheme -> DarkColors
		else -> LightColors
	}

	val systemUiController = rememberSystemUiController()
	SideEffect {
		systemUiController.setSystemBarsColor(
			color = colorScheme.surface,
			darkIcons = !useDarkTheme
		)
	}

	MaterialTheme(
		colorScheme = colorScheme,
		content = content
	)
}
