package it.palsoftware.pastiera.root

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.content.ContextCompat
import it.palsoftware.pastiera.SettingsManager

/**
 * The keyboard's backlight, with root (the Root page):
 * - Follows the screen: on while the screen is on, off when it goes off (as PhysiBoard does).
 * - Follows the screen's brightness: brighter screen, brighter keys.
 * - Flashes for notifications, the keyboard as a notification light.
 * The light is the phone's LED whose name says keyboard ("kb", "keyboard", "button-backlight").
 */
object KeyboardBacklight {
    const val KEY_FOLLOW_SCREEN = "root_backlight_follow_screen"
    const val KEY_FOLLOW_BRIGHTNESS = "root_backlight_follow_brightness"
    const val KEY_NOTIFICATION_FLASH = "root_backlight_notification_flash"

    @Volatile private var node: String? = null
    @Volatile private var maxBrightness: Int = 255
    private var receiver: BroadcastReceiver? = null
    private var observer: ContentObserver? = null

    private fun prefs(context: Context) = SettingsManager.getPreferences(context)
    fun followScreen(context: Context) = prefs(context).getBoolean(KEY_FOLLOW_SCREEN, false)
    fun followBrightness(context: Context) = prefs(context).getBoolean(KEY_FOLLOW_BRIGHTNESS, false)
    fun notificationFlash(context: Context) = prefs(context).getBoolean(KEY_NOTIFICATION_FLASH, false)

    /** The keyboard backlight's sysfs directory, found once. */
    fun findNode(): String? {
        node?.let { return it }
        val listing = RootShell.run("ls /sys/class/leds") ?: return null
        val name = listing.lines().map { it.trim() }.firstOrNull { entry ->
            val lower = entry.lowercase()
            lower.contains("keyboard") || lower.contains("kb") || lower.contains("button-backlight") || lower.contains("kpd")
        } ?: return null
        val path = "/sys/class/leds/$name"
        maxBrightness = RootShell.run("cat $path/max_brightness")?.trim()?.toIntOrNull() ?: 255
        node = path
        return path
    }

    fun set(level: Int) {
        Thread {
            val path = findNode() ?: return@Thread
            RootShell.run("echo ${level.coerceIn(0, maxBrightness)} > $path/brightness")
        }.start()
    }

    /** The level for the screen's brightness now (0 to 255 in Android's settings). */
    private fun levelForScreen(context: Context): Int {
        val screen = runCatching { Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) }.getOrDefault(128)
        return (screen / 255f * maxBrightness).toInt().coerceAtLeast(1)
    }

    /** Starts following the screen, as set (called as the keyboard starts; idempotent). */
    fun start(context: Context) {
        stop(context)
        if (!followScreen(context) && !followBrightness(context)) return
        if (!RootShell.probablyRooted()) return
        val app = context.applicationContext
        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> if (followScreen(ctx)) set(0)
                    Intent.ACTION_SCREEN_ON -> if (followScreen(ctx)) set(if (followBrightness(ctx)) levelForScreen(ctx) else maxBrightness)
                }
            }
        }.also {
            ContextCompat.registerReceiver(
                app, it, IntentFilter().apply { addAction(Intent.ACTION_SCREEN_ON); addAction(Intent.ACTION_SCREEN_OFF) },
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
        if (followBrightness(context)) {
            observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) = set(levelForScreen(app))
            }.also {
                app.contentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, it)
            }
            set(levelForScreen(app))
        }
    }

    fun stop(context: Context) {
        val app = context.applicationContext
        receiver?.let { runCatching { app.unregisterReceiver(it) } }
        receiver = null
        observer?.let { app.contentResolver.unregisterContentObserver(it) }
        observer = null
    }

    /** Three quick flashes for a notification, then back to where it was. */
    fun flash(context: Context) {
        if (!notificationFlash(context)) return
        Thread {
            val path = findNode() ?: return@Thread
            val before = RootShell.run("cat $path/brightness")?.trim()?.toIntOrNull() ?: 0
            repeat(3) {
                RootShell.run("echo $maxBrightness > $path/brightness")
                Thread.sleep(180)
                RootShell.run("echo 0 > $path/brightness")
                Thread.sleep(180)
            }
            RootShell.run("echo $before > $path/brightness")
        }.start()
    }
}
