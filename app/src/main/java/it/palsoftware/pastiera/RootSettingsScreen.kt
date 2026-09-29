package it.palsoftware.pastiera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import it.palsoftware.pastiera.root.KeyboardBacklight
import it.palsoftware.pastiera.root.RootShell
import it.palsoftware.pastiera.root.ScrollModule
import kotlinx.coroutines.Dispatchers
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
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            granted = RootShell.available()
            if (granted == true) moduleInstalled = ScrollModule.installed()
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
        }

        SettingsSectionDivider(stringResource(R.string.root_section_backlight))
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
