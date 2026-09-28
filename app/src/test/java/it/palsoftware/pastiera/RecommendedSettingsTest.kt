package it.palsoftware.pastiera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class RecommendedSettingsTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Before
    fun clear() {
        SettingsManager.getPreferences(context).edit().clear().commit()
    }

    @Test
    fun theRecommendedSettingsAreApplied() {
        assertTrue(RecommendedSettings.apply(context))
        assertTrue(SettingsManager.getPreferences(context).getBoolean(RecommendedSettings.PREF_APPLIED, false))
        assertTrue(SettingsManager.getSmartAltOffAfterOpening(context))
        assertTrue(SettingsManager.getSmartCtrlOffAfterShortcut(context))
        assertTrue(SettingsManager.getLedIndividualColorsEnabled(context))
        assertTrue(SettingsManager.getShiftBackspaceDelete(context))
        // Matters of taste stay as they are: the tutorial's "Your choices" asks
        assertTrue(SettingsManager.getEmojiSuggestionsEnabled(context))
        assertFalse(SettingsManager.getGifsEnabled(context))
        // Extras that need a permission or another app start off: the tutorial sets them up
        assertEquals(listOf("bitpit.launcher"), SettingsManager.getHiddenKeyboardApps(context))
        assertFalse(SettingsManager.getOneTimeCodesEnabled(context))
        assertEquals(SettingsManager.QUICK_LAUNCHER_BEHAVIOR_PASTIERA, SettingsManager.getQuickLauncherBehavior(context))
    }

    @Test
    fun nothingPersonalIsRecommended() {
        val keys = RecommendedSettings.values(titan2Elite = true).keys
        listOf(
            "app_enter_behavior_overrides", "sym_mappings_custom", "sym_mappings_page2_custom", "keyboard_theme_hardware",
            "menu_bar_buttons", "launcher_shortcuts", "hidden_keyboard_apps", "led_color_shift", "auto_correct_enabled"
        ).forEach { assertFalse(it, it in keys) }
        // Titan 2 Elite extras only on that phone
        assertFalse("trackpad_gestures_enabled" in RecommendedSettings.values(titan2Elite = false))
        assertTrue("trackpad_gestures_enabled" in keys)
    }

    @Test
    fun unitTestsKeepPastierasOwnDefaults() {
        assertFalse(RecommendedSettings.applyIfFreshInstall(context))
    }

    @Test
    fun devsChoiceIsTheDefaultVariationBar() {
        assertEquals(SettingsManager.STATIC_VARIATION_PRESET_DEV_CHOICE, SettingsManager.getStaticVariationBarPreset(context))
        assertEquals(
            SettingsManager.getDevChoiceStaticVariationBasePreset(),
            it.palsoftware.pastiera.data.variation.VariationRepository.loadStaticVariations(context.assets, context)
        )
        // The older on/off switch, set before presets existed, keeps its meaning
        SettingsManager.getPreferences(context).edit().putBoolean("static_variation_bar_mode", false).commit()
        assertEquals(SettingsManager.STATIC_VARIATION_PRESET_OFF, SettingsManager.getStaticVariationBarPreset(context))
    }

    @Test
    fun niagaraIsHiddenByDefault() {
        assertEquals(listOf("bitpit.launcher"), SettingsManager.getHiddenKeyboardApps(context))
    }

    @Test
    fun recommendedSettingsCountWhatDiffersThenMatchOnceApplied() {
        // A fresh start differs from the recommended configuration
        assertTrue(RecommendedSettings.differingSettings(context) > 0)
        assertTrue(RecommendedSettings.apply(context))
        assertEquals(0, RecommendedSettings.differingSettings(context))
        SettingsManager.setSmartAltOffAfterOpening(context, false)
        assertEquals(1, RecommendedSettings.differingSettings(context))
    }
}
