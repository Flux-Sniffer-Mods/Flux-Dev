package it.palsoftware.pastiera.core

import android.content.Context
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import it.palsoftware.pastiera.SettingsManager

/**
 * Defers spaces after configured punctuation until more text is actually typed.
 * This avoids trailing whitespace when punctuation ends a message.
 */
object DeferredPunctuationSpaceTracker {
    private const val NO_SPACE_BEFORE: String = ".,;:!?/\\)]}»›"

    @Volatile
    private var pending: Boolean = false

    /** Off in fields where a space after "." or "@" would break what's typed: see [appliesTo] */
    @Volatile
    private var enabled: Boolean = true

    /** Called as each field starts */
    fun startField(info: EditorInfo?) {
        enabled = appliesTo(info)
        pending = false
    }

    /**
     * Spaces after punctuation belong in prose only: not in email addresses, sign-in names,
     * web addresses, passwords, numbers or dates.
     */
    fun appliesTo(info: EditorInfo?): Boolean {
        if (info == null) return true
        val type = info.inputType
        if (type and InputType.TYPE_MASK_CLASS != InputType.TYPE_CLASS_TEXT) return type == InputType.TYPE_NULL
        val variation = type and InputType.TYPE_MASK_VARIATION
        if (variation in setOf(
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS,
                InputType.TYPE_TEXT_VARIATION_URI, InputType.TYPE_TEXT_VARIATION_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
                InputType.TYPE_TEXT_VARIATION_FILTER
            )) return false
        // Sign-in fields often only say so in their hint text or name
        val hints = (info.hintText?.toString().orEmpty() +
            " " + info.fieldName.orEmpty()).lowercase()
        return listOf("email", "e-mail", "username", "user name", "login", "sign in", "url", "website")
            .none { it in hints }
    }

    fun prepareForTextCommit(
        context: Context,
        inputConnection: InputConnection,
        text: CharSequence
    ): Boolean {
        val first = text.firstOrNull() ?: return false
        if (!enabled) {
            pending = false
            return false
        }
        if (first.isWhitespace()) {
            pending = false
            return false
        }

        val hadPending = pending
        var insertedSpace = false
        if (hadPending && first !in NO_SPACE_BEFORE) {
            inputConnection.commitText(" ", 1)
            pending = false
            insertedSpace = true
        }

        val configured = SettingsManager.getSpaceAfterPunctuation(context)
        pending = when {
            first in configured -> true
            hadPending && first in NO_SPACE_BEFORE -> true
            else -> false
        }
        return insertedSpace
    }

    fun clear() {
        pending = false
    }

    fun onTextCommitted(context: Context, text: CharSequence) {
        if (!enabled) return
        val first = text.firstOrNull() ?: return
        if (first in SettingsManager.getSpaceAfterPunctuation(context)) {
            pending = true
        }
    }

    internal fun isPending(): Boolean = pending
}
