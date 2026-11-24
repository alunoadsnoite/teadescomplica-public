package br.com.teadescomplica.feature.scenarios

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import br.com.teadescomplica.R

private enum class RightsGuide(
    @StringRes val title: Int,
    @StringRes val steps: Int,
    @StringRes val phrase: Int,
    val source: String,
) {
    Priority(
        R.string.rights_priority,
        R.string.rights_priority_steps,
        R.string.rights_priority_phrase,
        "https://www.planalto.gov.br/ccivil_03/leis/l10048.htm",
    ),
    School(
        R.string.rights_school,
        R.string.rights_school_steps,
        R.string.rights_school_phrase,
        "https://www.planalto.gov.br/ccivil_03/_ato2011-2014/2012/lei/l12764.htm",
    ),
    Health(
        R.string.rights_health,
        R.string.rights_health_steps,
        R.string.rights_health_phrase,
        "https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2015/lei/l13146.htm",
    ),
}

@Composable
internal fun RightsGuides(onDismiss: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf<RightsGuide?>(null) }
    var copied by rememberSaveable { mutableStateOf(false) }
    var sourceError by rememberSaveable { mutableStateOf(false) }
    val guide = selected
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(guide?.title ?: R.string.rights_guides)) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (guide == null) {
                    Text(stringResource(R.string.rights_guides_intro))
                    RightsGuide.entries.forEach { option ->
                        TextButton(onClick = {
                            selected = option
                            copied = false
                            sourceError = false
                        }) {
                            Text(stringResource(option.title))
                        }
                    }
                } else {
                    val phrase = stringResource(guide.phrase)
                    Text(stringResource(guide.steps))
                    Text(phrase, style = MaterialTheme.typography.bodyLarge)
                    TextButton(onClick = {
                        clipboard.setText(AnnotatedString(phrase))
                        copied = true
                    }) {
                        Text(stringResource(if (copied) R.string.rights_copied else R.string.rights_copy))
                    }
                    TextButton(onClick = {
                        sourceError = runCatching { uriHandler.openUri(guide.source) }.isFailure
                    }) {
                        Text(stringResource(R.string.rights_source))
                    }
                    if (sourceError) Text(stringResource(R.string.rights_source_error))
                    Text(stringResource(R.string.rights_reviewed), style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (guide == null) onDismiss() else selected = null
            }) {
                Text(stringResource(if (guide == null) R.string.rights_close else R.string.rights_back))
            }
        },
    )
}
