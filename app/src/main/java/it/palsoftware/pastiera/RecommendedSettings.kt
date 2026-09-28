package it.palsoftware.pastiera

import android.content.Context
import it.palsoftware.pastiera.inputmethod.DeviceSpecific

/**
 * The recommended settings: a short list of settings that suit nearly everyone, chosen from
 * a configuration made on a Titan 2 Elite, plus a few more on that phone. Applied on a fresh
 * install and from Privacy & system > Backup & restore. Anything that's a matter of taste
 * (auto-correct, GIFs, colours, what the emoji key opens) or needs a permission is left to the
 * tutorial's "Your choices" and "Extras" pages instead.
 */
object RecommendedSettings {
    const val PREF_APPLIED = "recommended_settings_applied"

    /** For everyone. */
    private val EVERYWHERE: Map<String, Any> = mapOf(
        // No automatic capitals in fields an app marks as restricted (codes, usernames)
        "auto_capitalize_restricted_fields" to true,
        // Exact typing also where an app asks for no suggestions (SSH clients, code editors)
        "exact_typing_no_suggestions" to true,
        // Suggestions allow for neighbouring keys on the keyboard
        "use_keyboard_proximity" to true,
        // Shift + Backspace deletes forwards
        "shift_backspace_delete" to true,
        // Alt after an opening bracket, and Ctrl after one shortcut, switch themselves off
        "smart_alt_off_after_opening" to true,
        "smart_ctrl_off_after_shortcut" to true,
        "quick_launcher_pill_mode" to true,
        "led_individual_colors" to true,
        "led_locked_animation" to true,
        // :shortcodes: expand with Enter, or with Space on an exact match
        "emoji_symbols_accept_with_enter" to true,
        "emoji_symbols_exact_on_space" to true
    )

    /** On the Titan 2 Elite: its trackpad swipes and the compact bar fitted to its screen. */
    private val TITAN_2_ELITE: Map<String, Any> = mapOf(
        "trackpad_gestures_enabled" to true,
        "trackpad_provider" to SettingsManager.TRACKPAD_PROVIDER_NATIVE_IME,
        "trackpad_suggestion_swipe_directions" to true,
        "trackpad_swipe_down_deletes_word" to true,
        "swipe_to_delete" to false,
        "pastierina_mode_override" to "pastierina",
        "pastierina_status_bar_slots_left" to "[\"microphone\"]",
        "pastierina_status_bar_slots_right" to "[\"hamburger\"]"
    )

    internal fun values(titan2Elite: Boolean): Map<String, Any> =
        if (titan2Elite) EVERYWHERE + TITAN_2_ELITE else EVERYWHERE

    private fun values(): Map<String, Any> = values(DeviceSpecific.isTitan2EliteDevice())

    /** On a fresh install (no settings yet), applies the recommended settings. */
    fun applyIfFreshInstall(context: Context): Boolean {
        // Unit tests start every app from scratch and expect Pastiera's own defaults
        if (android.os.Build.FINGERPRINT == "robolectric") return false
        if (SettingsManager.getPreferences(context).all.isNotEmpty()) return false
        return apply(context)
    }

    fun apply(context: Context): Boolean {
        val editor = SettingsManager.getPreferences(context).edit()
        values().forEach { (key, value) ->
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is Int -> editor.putInt(key, value)
                is Float -> editor.putFloat(key, value)
                is String -> editor.putString(key, value)
            }
        }
        return editor.putBoolean(PREF_APPLIED, true).commit()
    }

    /** How many of the recommended settings you have set differently, or not at all. */
    fun differingSettings(context: Context): Int {
        val current = SettingsManager.getPreferences(context).all
        return values().count { (key, value) -> current[key] != value }
    }
}
