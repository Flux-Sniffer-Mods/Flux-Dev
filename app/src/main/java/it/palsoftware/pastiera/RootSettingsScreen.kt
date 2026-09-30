package it.palsoftware.pastiera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import it.palsoftware.pastiera.root.KeyboardBacklight
import it.palsoftware.pastiera.root.RootShell
import it.palsoftware.pastiera.root.ScrollModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Root: what Flux Keyboard can do on a rooted phone. Only listed on phones with su; opening it
 * asks for root once.
 */
@Composable
fun RootSettingsScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = SettingsManager.getPreferences(context)
    var granted by remember { mutableStateOf<Boolean?>(null) }
    var moduleInstalled by remember { mutableStateOf(false) }
    var moduleReleases by remember { mutableStateOf<Boolean?>(null) }
    var backlightSupported by remember { mutableStateOf(false) }
    var backlightLevel by remember { mutableStateOf(100f) }
    var backlightTimeout by remember { mutableStateOf<Float?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            granted = RootShell.available()
            if (granted == true) {
                moduleInstalled = ScrollModule.installed()
                if (moduleInstalled) moduleReleases = ScrollModule.hasReleaseOption()
                backlightSupported = KeyboardBacklight.supported()
                if (backlightSupported) {
                    (KeyboardBacklight.brightness() ?: KeyboardBacklight.chosen(context).takeIf { it >= 0 })?.let { backlightLevel = it.toFloat() }
                    backlightTimeout = KeyboardBacklight.timeoutSeconds()?.coerceIn(1, 60)?.toFloat()
                }
            }
        }
    }
    fun pref(key: String, default: Boolean = false) = prefs.getBoolean(key, default)
    var rootTrackpad by remember { mutableStateOf(pref(it.palsoftware.pastiera.inputmethod.PhysicalKeyboardInputMethodService.ROOT_TRACKPAD_KEY)) }
    var pauseModule by remember { mutableStateOf(pref(ScrollModule.KEY_PAUSE_WHILE_TYPING, true)) }
    var followScreen by remember { mutableStateOf(pref(KeyboardBacklight.KEY_FOLLOW_SCREEN)) }
    var followBrightness by remember { mutableStateOf(pref(KeyboardBacklight.KEY_FOLLOW_BRIGHTNESS)) }
    var flash by remember { mutableStateOf(pref(KeyboardBacklight.KEY_NOTIFICATION_FLASH)) }

    FluxScreenScaffold(stringResource(R.string.root_title), onBack, modifier) {
        FluxNote(
            stringResource(
                when (granted) {
                    null -> R.string.root_checking
                    true -> R.string.root_note
                    false -> R.string.root_not_granted
                }
            )
        )
        if (granted != true) return@FluxScreenScaffold

        SettingsSectionDivider(stringResource(R.string.root_section_trackpad))
        FluxSwitchRow(
            linkId = "root.trackpad",
            title = stringResource(R.string.root_trackpad_title),
            description = stringResource(R.string.root_trackpad_description),
            checked = rootTrackpad,
            onCheckedChange = { on ->
                rootTrackpad = on
                prefs.edit().putBoolean(it.palsoftware.pastiera.inputmethod.PhysicalKeyboardInputMethodService.ROOT_TRACKPAD_KEY, on).apply()
            }
        )
        if (rootTrackpad) {
            FluxSwitchRow(
                linkId = "root.scroll_module",
                title = stringResource(R.string.root_scroll_module_title),
                description = stringResource(
                    if (moduleInstalled) R.string.root_scroll_module_description else R.string.root_scroll_module_missing
                ),
                checked = pauseModule,
                onCheckedChange = { on -> pauseModule = on; prefs.edit().putBoolean(ScrollModule.KEY_PAUSE_WHILE_TYPING, on).apply() }
            )
            if (moduleInstalled) {
                FluxActionRow(
                    linkId = "root.scroll_module_release",
                    title = stringResource(R.string.root_scroll_module_release_title),
                    description = stringResource(
                        when (moduleReleases) {
                            true -> R.string.root_scroll_module_release_on
                            false -> R.string.root_scroll_module_release_off
                            null -> R.string.root_checking
                        }
                    )
                ) {
                    if (moduleReleases == false) {
                        scope.launch {
                            val ok = withContext(Dispatchers.IO) { ScrollModule.addReleaseOption() }
                            moduleReleases = ok
                            if (!ok) android.widget.Toast.makeText(context, R.string.root_scroll_module_release_failed, android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        FluxActionRow(
            linkId = "root.scroll_module_link",
            title = stringResource(R.string.root_scroll_module_link_title),
            description = stringResource(R.string.root_scroll_module_link_description)
        ) {
            runCatching {
                context.startActivity(
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(ScrollModule.MODULE_URL))
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }

        SettingsSectionDivider(stringResource(R.string.root_section_backlight))
        if (!backlightSupported) FluxNote(stringResource(R.string.root_backlight_unsupported))
        if (backlightSupported && !followBrightness) {
            RootSliderRow(
                linkId = "root.backlight_level",
                title = stringResource(R.string.root_backlight_level_title),
                value = backlightLevel,
                range = 0f..100f,
                label = "${backlightLevel.toInt()}",
                onChange = { backlightLevel = it },
                onDone = { KeyboardBacklight.setChosen(context, backlightLevel.toInt()) }
            )
        }
        val timeout = backlightTimeout
        if (timeout != null && !followScreen && !followBrightness) {
            RootSliderRow(
                linkId = "root.backlight_timeout",
                title = stringResource(R.string.root_backlight_timeout_title),
                value = timeout,
                range = 1f..60f,
                label = stringResource(R.string.root_backlight_timeout_value, timeout.toInt()),
                onChange = { backlightTimeout = it },
                onDone = { KeyboardBacklight.setTimeoutSeconds(timeout.toInt()) }
            )
        }
        FluxSwitchRow(
            linkId = "root.backlight_screen",
            title = stringResource(R.string.root_backlight_screen_title),
            description = stringResource(R.string.root_backlight_screen_description),
            checked = followScreen,
            onCheckedChange = { on ->
                followScreen = on
                prefs.edit().putBoolean(KeyboardBacklight.KEY_FOLLOW_SCREEN, on).apply()
                KeyboardBacklight.start(context)
            }
        )
        FluxSwitchRow(
            linkId = "root.backlight_brightness",
            title = stringResource(R.string.root_backlight_brightness_title),
            description = stringResource(R.string.root_backlight_brightness_description),
            checked = followBrightness,
            onCheckedChange = { on ->
                followBrightness = on
                prefs.edit().putBoolean(KeyboardBacklight.KEY_FOLLOW_BRIGHTNESS, on).apply()
                KeyboardBacklight.start(context)
            }
        )
        FluxSwitchRow(
            linkId = "root.backlight_flash",
            title = stringResource(R.string.root_backlight_flash_title),
            description = stringResource(R.string.root_backlight_flash_description),
            checked = flash,
            onCheckedChange = { on -> flash = on; prefs.edit().putBoolean(KeyboardBacklight.KEY_NOTIFICATION_FLASH, on).apply() }
        )

        SettingsSectionDivider(stringResource(R.string.root_section_shortcuts))
        FluxNote(stringResource(R.string.root_shortcuts_note))
    }
}

/** A title and a slider with its value. */
@Composable
private fun RootSliderRow(
    linkId: String,
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    label: String,
    onChange: (Float) -> Unit,
    onDone: () -> Unit
) {
    androidx.compose.material3.Surface(modifier = Modifier.fillMaxWidth().settingRow(linkId)) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
            androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                androidx.compose.material3.Text(
                    title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                androidx.compose.material3.Text(label, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
            }
            androidx.compose.material3.Slider(
                value = value,
                onValueChange = onChange,
                onValueChangeFinished = onDone,
                valueRange = range
            )
        }
    }
}
