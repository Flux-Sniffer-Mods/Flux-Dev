package it.palsoftware.pastiera.root

import android.content.Context
import it.palsoftware.pastiera.SettingsManager

/**
 * The Titan 2 better keyboard scroll module (vhqtvn/titan2-better-keyboard-scroll-module): it
 * takes the keyboard's touch pad for itself (real scrolling where you touch), so keyboard swipes
 * never reach the keyboard app. With root, Flux Keyboard pauses it while you type (the scroll
 * assistant setting it follows, persist.sys.scroll_assistant, off; the module needs its -R
 * option to let go of the pad then) and reads the pad itself for suggestion swipes; leaving the
 * text field puts scrolling back as it was.
 */
object ScrollModule {
    const val KEY_PAUSE_WHILE_TYPING = "root_scroll_module_pause_while_typing"
    private const val PROPERTY = "persist.sys.scroll_assistant"

    @Volatile private var installed: Boolean? = null
    @Volatile private var pausedFrom: String? = null

    fun pauseWhileTyping(context: Context): Boolean =
        SettingsManager.getPreferences(context).getBoolean(KEY_PAUSE_WHILE_TYPING, true)

    /** Whether the module is installed (asked once, as root). */
    fun installed(): Boolean {
        installed?.let { return it }
        val found = RootShell.run("ls /data/adb/modules")?.lines()?.any { it.contains("scroll", ignoreCase = true) && it.contains("titan", ignoreCase = true) || it.contains("tpscroll", ignoreCase = true) } == true ||
            RootShell.run("pidof tpscroll")?.isNotBlank() == true
        installed = found
        return found
    }

    const val MODULE_URL = "https://github.com/vhqtvn/titan2-better-keyboard-scroll-module"

    /** The module's service.sh, where its options (ARGS) are (null when not found). */
    fun serviceScript(): String? =
        RootShell.run("grep -l tpscroll /data/adb/modules/*/service.sh")?.lines()?.firstOrNull { it.isNotBlank() }?.trim()

    /** Whether the module lets go of the pad while paused (its -R option). */
    fun hasReleaseOption(): Boolean {
        val script = serviceScript() ?: return false
        return RootShell.run("grep '^ARGS=' $script")?.let { Regex("""(^|["' =])-R(["' ]|$)""").containsMatchIn(it) } == true
    }

    /**
     * Adds -R to the module's options and restarts it, so it lets go of the pad while paused.
     * Returns whether the option is there now.
     */
    fun addReleaseOption(): Boolean {
        val script = serviceScript() ?: return false
        if (hasReleaseOption()) return true
        // No options line: it can't be added safely (it has to come before the module starts)
        val line = RootShell.run("grep '^ARGS=' $script")?.lines()?.firstOrNull()?.trim() ?: return false
        val edit = when {
            line.startsWith("ARGS=\"") -> "sed -i 's/^ARGS=\"/ARGS=\"-R /' $script"
            line.startsWith("ARGS='") -> "sed -i \"s/^ARGS='/ARGS='-R /\" $script"
            else -> "sed -i 's/^ARGS=/ARGS=-R\\ /' $script"
        }
        RootShell.run(edit)
        // Restart it with the new options
        RootShell.run("pkill -f tpscroll; pkill -f tpinject; (setsid sh $script >/dev/null 2>&1 &)")
        return hasReleaseOption()
    }

    /** A text field opened: scrolling pauses so swipes pick suggestions. */
    fun onTypingStarted(context: Context) {
        if (!pauseWhileTyping(context) || pausedFrom != null) return
        Thread {
            if (!installed()) return@Thread
            val current = RootShell.run("getprop $PROPERTY")?.trim().orEmpty()
            if (current == "0") return@Thread
            pausedFrom = current.ifEmpty { "1" }
            RootShell.run("setprop $PROPERTY 0")
        }.start()
    }

    /** The text field closed: scrolling as it was. */
    fun onTypingEnded() {
        val restore = pausedFrom ?: return
        pausedFrom = null
        RootShell.runAsync("setprop $PROPERTY $restore")
    }
}
