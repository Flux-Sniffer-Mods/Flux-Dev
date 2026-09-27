package it.palsoftware.pastiera

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import android.widget.Toast

/** Flux Keyboard: a picture behind the keyboard, with Auto colours and the keys' opacity. */
@Composable
fun KeyboardBackgroundImageSettings() {
    val context = LocalContext.current
    var hasImage by remember { mutableStateOf(KeyboardBackgroundImage.exists(context)) }
    var autoColours by remember { mutableStateOf(SettingsManager.getKeyboardBackgroundAutoColours(context)) }
    var opacity by remember { mutableFloatStateOf(SettingsManager.getKeyboardBackgroundKeyOpacity(context).toFloat()) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            if (KeyboardBackgroundImage.save(context, uri)) hasImage = true
            else Toast.makeText(context, R.string.keyboard_background_image_failed, Toast.LENGTH_SHORT).show()
        }
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().settingRow(SettingLinkIds.KEYBOARD_BACKGROUND_IMAGE),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.keyboard_background_image_title), style = MaterialTheme.typography.bodyLarge)
                Text(
                    stringResource(R.string.keyboard_background_image_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (hasImage) {
                TextButton(onClick = {
                    KeyboardBackgroundImage.remove(context)
                    hasImage = false
                }) { Text(stringResource(R.string.keyboard_background_image_remove)) }
            }
            OutlinedButton(onClick = { picker.launch("image/*") }) {
                Text(stringResource(if (hasImage) R.string.keyboard_background_image_change else R.string.keyboard_background_image_choose))
            }
        }
        if (hasImage) {
            Row(
                modifier = Modifier.fillMaxWidth().settingRow(SettingLinkIds.KEYBOARD_BACKGROUND_AUTO_COLOURS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.keyboard_background_auto_colours_title), style = MaterialTheme.typography.bodyLarge)
                    Text(
                        stringResource(R.string.keyboard_background_auto_colours_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = autoColours, onCheckedChange = {
                    autoColours = it
                    SettingsManager.setKeyboardBackgroundAutoColours(context, it)
                })
            }
            if (autoColours) {
                Column(modifier = Modifier.fillMaxWidth().settingRow(SettingLinkIds.KEYBOARD_BACKGROUND_KEY_OPACITY)) {
                    Text(
                        stringResource(R.string.keyboard_background_key_opacity_title, opacity.toInt()),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Slider(
                        value = opacity,
                        onValueChange = { opacity = it.toInt().toFloat() },
                        onValueChangeFinished = { SettingsManager.setKeyboardBackgroundKeyOpacity(context, opacity.toInt()) },
                        valueRange = 0f..100f
                    )
                }
            }
        }
    }
}
