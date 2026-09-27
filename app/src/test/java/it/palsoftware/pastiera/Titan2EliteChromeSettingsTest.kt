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
class Titan2EliteChromeSettingsTest {

    private val context get() = RuntimeEnvironment.getApplication()

    @Before
    fun reset() {
        SettingsManager.getPreferences(context).edit().clear().commit()
    }

    @Test
    fun defaultsKeepTheCurrentLook() {
        assertFalse(SettingsManager.getTitan2EliteFillCorners(context))
        assertFalse(SettingsManager.getTitan2EliteStraightOuterButtons(context))
        assertEquals(0, SettingsManager.getTitan2EliteStatusBarLiftDp(context))
        assertEquals(0, SettingsManager.getTitan2EliteStatusBarLiftPx(context))
    }

    @Test
    fun fillCornersIsStoredAndOffWhileContoured() {
        SettingsManager.setTitan2EliteFillCorners(context, true)
        assertFalse(SettingsManager.getTitan2EliteFillCorners(context))
        SettingsManager.setTitan2EliteContourLeds(context, false)
        assertTrue(SettingsManager.getTitan2EliteFillCorners(context))
    }

    @Test
    fun straightButtonsLiftTheBarByAFixedAmount() {
        SettingsManager.setTitan2EliteContourLeds(context, false)
        assertTrue(SettingsManager.getTitan2EliteStraightOuterButtons(context))
        assertEquals(SettingsManager.TITAN2_ELITE_DEFAULT_LIFT_DP, SettingsManager.getTitan2EliteStatusBarLiftDp(context))
        val density = context.resources.displayMetrics.density
        assertEquals(
            Math.round(SettingsManager.TITAN2_ELITE_DEFAULT_LIFT_DP * density),
            SettingsManager.getTitan2EliteStatusBarLiftPx(context)
        )
    }
}
