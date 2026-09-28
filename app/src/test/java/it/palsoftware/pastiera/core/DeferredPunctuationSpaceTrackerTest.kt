package it.palsoftware.pastiera.core

import android.content.Context
import android.view.View
import android.view.inputmethod.BaseInputConnection
import it.palsoftware.pastiera.SettingsManager
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
class DeferredPunctuationSpaceTrackerTest {

    private lateinit var context: Context
    private lateinit var inputConnection: FakeInputConnection

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences("pastiera_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        SettingsManager.setSpaceAfterPunctuation(context, "?!")
        DeferredPunctuationSpaceTracker.clear()
        DeferredPunctuationSpaceTracker.startField(null)
        inputConnection = FakeInputConnection(context)
    }

    @Test
    fun noSpacesInEmailAndSignInFields() {
        fun field(type: Int, hint: String? = null) = android.view.inputmethod.EditorInfo().apply {
            inputType = type
            hintText = hint
        }
        val text = android.text.InputType.TYPE_CLASS_TEXT
        assertTrue(DeferredPunctuationSpaceTracker.appliesTo(field(text)))
        assertFalse(DeferredPunctuationSpaceTracker.appliesTo(
            field(text or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)))
        assertFalse(DeferredPunctuationSpaceTracker.appliesTo(
            field(text or android.text.InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS)))
        assertFalse(DeferredPunctuationSpaceTracker.appliesTo(field(text, "Email or phone")))
        assertFalse(DeferredPunctuationSpaceTracker.appliesTo(field(android.text.InputType.TYPE_CLASS_NUMBER)))

        DeferredPunctuationSpaceTracker.startField(field(text or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS))
        commit("?")
        commit("a")
        assertEquals("?a", inputConnection.text)
    }

    @Test
    fun configuredPunctuationDefersSpaceUntilNextText() {
        commit("?")

        assertEquals("?", inputConnection.text)
        assertTrue(DeferredPunctuationSpaceTracker.isPending())

        commit("W")

        assertEquals("? W", inputConnection.text)
        assertFalse(DeferredPunctuationSpaceTracker.isPending())
    }

    @Test
    fun explicitSpaceConsumesPendingWithoutDuplication() {
        commit("!")
        commit(" ")

        assertEquals("! ", inputConnection.text)
        assertFalse(DeferredPunctuationSpaceTracker.isPending())
    }

    @Test
    fun punctuationSequenceKeepsSpaceDeferred() {
        commit("?")
        commit("!")
        commit("W")

        assertEquals("?! W", inputConnection.text)
    }

    @Test
    fun clearingBeforeSendLeavesNoTrailingSpace() {
        commit("!")

        DeferredPunctuationSpaceTracker.clear()

        assertEquals("!", inputConnection.text)
        assertFalse(DeferredPunctuationSpaceTracker.isPending())
    }

    private fun commit(text: String) {
        DeferredPunctuationSpaceTracker.prepareForTextCommit(context, inputConnection, text)
        inputConnection.commitText(text, 1)
    }

    private class FakeInputConnection(context: Context) : BaseInputConnection(View(context), true) {
        private val buffer = StringBuilder()

        val text: String
            get() = buffer.toString()

        override fun commitText(text: CharSequence?, newCursorPosition: Int): Boolean {
            buffer.append(text ?: "")
            return true
        }
    }
}
