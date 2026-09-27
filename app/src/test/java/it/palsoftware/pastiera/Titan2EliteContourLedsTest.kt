package it.palsoftware.pastiera

import org.junit.Assert.assertEquals
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
    fun ledOffsetIsCalibratedAndPushesTheButtonsInward() {
        val context = RuntimeEnvironment.getApplication()
        val base = it.palsoftware.pastiera.inputmethod.ui.LedStatusView.contourButtonInsetPx(context)
        T2eCornerCalibration(ledOffsetPx = 10f).save(context)
        assertEquals(10f, T2eCornerCalibration.readSaved(context).ledOffsetPx)
        val moved = it.palsoftware.pastiera.inputmethod.ui.LedStatusView.contourButtonInsetPx(context)
        assertTrue(moved > base)
        assertEquals(10f - T2eCornerCalibration().ledOffsetPx, moved - base, 0.01f)
    }
}
