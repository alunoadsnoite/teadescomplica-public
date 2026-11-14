package br.com.teadescomplica.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import br.com.teadescomplica.core.datastore.ThemePreference

/**
 * Tema do aplicativo.
 *
 * Mapeia a preferência de tema (sistema/claro/escuro) para um ColorScheme do Material 3,
 * respeitando o tema do dispositivo quando selecionado "sistema".
 *
 * Não há animações obrigatórias; o consumidor controla o uso de animações via preferência de
 * movimento reduzido.
 */
@Composable
fun TeaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme =
        if (darkTheme)
            darkColorScheme(
                primary = TeaColors.DarkPrimary,
                onPrimary = TeaColors.DarkOnPrimary,
                primaryContainer = TeaColors.DarkPrimaryContainer,
                onPrimaryContainer = TeaColors.DarkOnPrimaryContainer,
                secondary = TeaColors.DarkSecondary,
                onSecondary = TeaColors.DarkOnSecondary,
                background = TeaColors.DarkBackground,
                surface = TeaColors.DarkSurface,
                surfaceVariant = TeaColors.DarkSurfaceVariant,
                onSurface = TeaColors.DarkOnSurface,
                onSurfaceVariant = TeaColors.DarkOnSurfaceVariant,
                outline = TeaColors.DarkOutline,
                error = TeaColors.DarkError,
                onError = TeaColors.DarkOnError,
            )
        else
            lightColorScheme(
                primary = TeaColors.LightPrimary,
                onPrimary = TeaColors.LightOnPrimary,
                primaryContainer = TeaColors.LightPrimaryContainer,
                onPrimaryContainer = TeaColors.LightOnPrimaryContainer,
                secondary = TeaColors.LightSecondary,
                onSecondary = TeaColors.LightOnSecondary,
                background = TeaColors.LightBackground,
                surface = TeaColors.LightSurface,
                surfaceVariant = TeaColors.LightSurfaceVariant,
                onSurface = TeaColors.LightOnSurface,
                onSurfaceVariant = TeaColors.LightOnSurfaceVariant,
                outline = TeaColors.LightOutline,
                error = TeaColors.LightError,
                onError = TeaColors.LightOnError,
            )

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.MaterialTheme.typography,
        shapes = androidx.compose.material3.MaterialTheme.shapes,
        content = content,
    )
}

/** Resolve o tema efetivo (claro/escuro) a partir da preferência e do sistema. */
@Composable
fun rememberEffectiveDarkTheme(themePreference: ThemePreference): Boolean {
    val systemDark = isSystemInDarkTheme()
    val effective =
        when (themePreference) {
            ThemePreference.LIGHT -> false
            ThemePreference.DARK -> true
            ThemePreference.SYSTEM -> systemDark
        }
    return remember(effective) { effective }
}

/**
 * Wrapper que aplica o tema com base na preferência persistida.
 *
 * Deve ser usado na raiz da navegação para que o tema seja aplicado a todas as telas e restaurado
 * após reiniciar o aplicativo.
 */
@Composable
fun TeaAppTheme(themePreference: ThemePreference, content: @Composable () -> Unit) {
    val effectiveDark = rememberEffectiveDarkTheme(themePreference)
    TeaTheme(darkTheme = effectiveDark) { content() }
}
