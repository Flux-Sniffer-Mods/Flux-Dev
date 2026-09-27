package it.palsoftware.pastiera

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Titan2EliteContourLedsTest {
    @Test
    fun contourLedsRoundTheButtonsAndDropTheLiftUntilTurnedOff() {
        val context = RuntimeEnvironment.getApplication()
        SettingsManager.setTitan2EliteStraightOuterButtons(context, true)
        SettingsManager.setTitan2EliteStatusBarLiftDp(context, 7)
        assertFalse(SettingsManager.getTitan2EliteContourLeds(context))

        SettingsManager.setTitan2EliteContourLeds(context, true)
        assertFalse(SettingsManager.getTitan2EliteStraightOuterButtons(context))
        assertEquals(0, SettingsManager.getTitan2EliteStatusBarLiftDp(context))

        SettingsManager.setTitan2EliteContourLeds(context, false)
        assertTrue(SettingsManager.getTitan2EliteStraightOuterButtons(context))
        assertEquals(7, SettingsManager.getTitan2EliteStatusBarLiftDp(context))
    }
}
