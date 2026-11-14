@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.core.designsystem

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

/** Allow titles and actions to wrap at large font sizes instead of clipping a fixed-height bar. */
@Composable
fun TeaTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    if (LocalConfiguration.current.fontScale < 1.5f) {
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors,
        )
    } else {
        Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    navigationIcon()
                    Box(Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        ProvideTextStyle(MaterialTheme.typography.titleLarge) { title() }
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    content = actions,
                )
            }
        }
    }
}
