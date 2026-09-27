package it.palsoftware.pastiera

import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KeyboardBackgroundImageTest {
    private val theme = SettingsManager.KeyboardThemeSettings(
        background = Color.DKGRAY, divider = Color.GRAY, normalKey = Color.RED, specialKey = Color.BLUE,
        textAndIcons = Color.GREEN, ledInactive = 0, ledActive = 0, ledLocked = 0, accent = Color.YELLOW
    )

    @Test
    fun brightPictureGetsDarkSeeThroughKeysAndDarkOrLightTextToMatch() {
        val t = KeyboardBackgroundImage.recolour(theme, luminance = 0.8, autoColours = true, keyOpacityPercent = 40)
        assertEquals(Color.TRANSPARENT, t.background)
        assertEquals(Color.argb(102, 0, 0, 0), t.normalKey)
        assertTrue(Color.alpha(t.specialKey) > Color.alpha(t.normalKey))
        assertEquals(Color.BLACK, t.textAndIcons)
        assertEquals(Color.YELLOW, t.accent)
    }

    @Test
    fun darkPictureGetsLightSeeThroughKeys() {
        val t = KeyboardBackgroundImage.recolour(theme, luminance = 0.02, autoColours = true, keyOpacityPercent = 25)
        assertEquals(Color.argb(63, 255, 255, 255), t.normalKey)
        assertEquals(Color.WHITE, t.textAndIcons)
    }

    @Test
    fun solidKeysFlipTheText() {
        // Fully opaque black keys over a bright picture need white text
        val t = KeyboardBackgroundImage.recolour(theme, luminance = 0.9, autoColours = true, keyOpacityPercent = 100)
        assertEquals(Color.WHITE, t.textAndIcons)
    }

    @Test
    fun withoutAutoColoursOnlyTheBackgroundClears() {
        val t = KeyboardBackgroundImage.recolour(theme, luminance = 0.9, autoColours = false, keyOpacityPercent = 40)
        assertEquals(theme.copy(background = Color.TRANSPARENT), t)
    }
}
