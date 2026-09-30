package it.palsoftware.pastiera.root

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import it.palsoftware.pastiera.SettingsManager
import java.io.File
import java.util.concurrent.Executors

/**
 * The keyboard's backlight, with root (the Root page):
 * - Brightness and how long it stays lit after a key press.
 * - Follows the screen: on while the screen is on, off when it goes off.
 * - Follows the screen's brightness, fades included: brighter screen, brighter keys.
 * - Flashes for notifications, the keyboard as a notification light.
 *
 * On the Titan 2 the light isn't an LED under /sys/class/leds: it's behind Unihertz's vendor
 * service, the same one its Settings slider uses:
 *   service call agui_functional_service 2 s16 <key> s16 <value>   writes a key
 *   service call agui_functional_service 1 s16 <key>               reads one back
 * with keyboard_led_brightness (0 to 100) and keyboard_brightness_timeout (milliseconds).
 * Other phones use their keyboard LED under /sys/class/leds, if they have one.
 */
object KeyboardBacklight {
    const val KEY_FOLLOW_SCREEN = "root_backlight_follow_screen"
    const val KEY_FOLLOW_BRIGHTNESS = "root_backlight_follow_brightness"
    const val KEY_NOTIFICATION_FLASH = "root_backlight_notification_flash"
    private const val KEY_SAVED_TIMEOUT = "root_backlight_saved_timeout"

    private const val SERVICE = "agui_functional_service"
    private const val BRIGHTNESS = "keyboard_led_brightness"
    private const val TIMEOUT = "keyboard_brightness_timeout"
    /** While following the screen the light stays lit (an hour), else it would time out mid-use. */
    private const val FOLLOW_TIMEOUT_MS = 3_600_000
    private const val SCREEN_NODE = "/sys/class/leds/lcd-backlight"
    private const val POLL_MS = 150L
    private const val IDLE_POLL_MS = 1_000L

    private sealed class Route {
        object Vendor : Route()
        data class Led(val path: String, val max: Int) : Route()
        object None : Route()
    }

    @Volatile private var route: Route? = null
    @Volatile private var lastLevel = -1
    private val worker = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var receiver: BroadcastReceiver? = null
    private var poller: Runnable? = null

    private fun prefs(context: Context) = SettingsManager.getPreferences(context)
    fun followScreen(context: Context) = prefs(context).getBoolean(KEY_FOLLOW_SCREEN, false)
    fun followBrightness(context: Context) = prefs(context).getBoolean(KEY_FOLLOW_BRIGHTNESS, false)
    fun notificationFlash(context: Context) = prefs(context).getBoolean(KEY_NOTIFICATION_FLASH, false)

    private fun vendorGet(key: String): String? =
        RootShell.run("service call $SERVICE 1 s16 $key")
            // Parcel dump: the string sits between single quotes, dots for padding
            ?.lines()?.filter { "'" in it }?.joinToString("") { it.substringAfter("'").substringBefore("'") }
            ?.replace(".", "")?.replace(" ", "")?.takeIf { it.isNotEmpty() }

    private fun vendorSet(key: String, value: Int) {
        RootShell.run("service call $SERVICE 2 s16 $key s16 $value")
    }

    /** How the light is reached on this phone, found once (as root). */
    private fun route(): Route {
        route?.let { return it }
        val found = if (RootShell.run("service list")?.contains(SERVICE) == true && vendorGet(BRIGHTNESS) != null) {
            Route.Vendor
        } else {
            RootShell.run("ls /sys/class/leds")?.lines()?.map { it.trim() }?.firstOrNull { entry ->
                val lower = entry.lowercase()
                lower.contains("keyboard") || lower.contains("kbd") || lower.contains("button-backlight") || lower.contains("kpd")
            }?.let { name ->
                val path = "/sys/class/leds/$name"
                Route.Led(path, RootShell.run("cat $path/max_brightness")?.trim()?.toIntOrNull() ?: 255)
            } ?: Route.None
        }
        route = found
        return found
    }

    /** Whether this phone has a keyboard light Flux Keyboard can reach. */
    fun supported(): Boolean = route() != Route.None

    /** The brightness now, 0 to 100 (null when it can't be read). */
    fun brightness(): Int? = when (val r = route()) {
        Route.Vendor -> vendorGet(BRIGHTNESS)?.toIntOrNull()
        is Route.Led -> RootShell.run("cat ${r.path}/brightness")?.trim()?.toIntOrNull()?.let { it * 100 / r.max.coerceAtLeast(1) }
        Route.None -> null
    }

    /** Seconds the light stays lit after a key press (Titan 2 only; null elsewhere). */
    fun timeoutSeconds(): Int? =
        if (route() == Route.Vendor) vendorGet(TIMEOUT)?.toIntOrNull()?.let { it / 1000 } else null

    /** Sets the brightness, 0 to 100. */
    fun set(level: Int) {
        val clamped = level.coerceIn(0, 100)
        worker.execute { apply(clamped) }
    }

    private fun apply(level: Int) {
        if (level == lastLevel) return
        when (val r = route()) {
            Route.Vendor -> vendorSet(BRIGHTNESS, level)
            is Route.Led -> RootShell.run("echo ${level * r.max / 100} > ${r.path}/brightness")
            Route.None -> return
        }
        lastLevel = level
    }

    /** The brightness chosen on the Root page (the level used when not following the screen's). */
    fun setChosen(context: Context, level: Int) {
        prefs(context).edit().putInt("root_backlight_level", level.coerceIn(0, 100)).apply()
        lastLevel = -1
        set(level)
    }

    fun chosen(context: Context): Int = prefs(context).getInt("root_backlight_level", -1)

    fun setTimeoutSeconds(seconds: Int) {
        worker.execute { if (route() == Route.Vendor) vendorSet(TIMEOUT, seconds.coerceIn(1, 3600) * 1000) }
    }

    /** The screen's brightness as a share of its maximum, fades included (0 to 1). */
    private fun screenShare(context: Context): Float {
        // The panel's own level ramps with the screen's fades; readable without root on most phones
        val panel = runCatching {
            val now = File("$SCREEN_NODE/brightness").readText().trim().toInt()
            val max = File("$SCREEN_NODE/max_brightness").readText().trim().toInt()
            now.toFloat() / max.coerceAtLeast(1)
        }.getOrNull()
        if (panel != null) return panel.coerceIn(0f, 1f)
        val setting = runCatching { Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) }.getOrDefault(128)
        return (setting / 255f).coerceIn(0f, 1f)
    }

    private fun levelForScreen(context: Context): Int {
        val share = screenShare(context)
        return if (share <= 0f) 0 else (share * 100).toInt().coerceIn(1, 100)
    }

    private fun onLevel(context: Context): Int =
        if (followBrightness(context)) levelForScreen(context) else chosen(context).takeIf { it >= 0 } ?: 100

    /** Starts following the screen, as set (called as the keyboard starts; idempotent). */
    fun start(context: Context) {
        stop(context)
        val app = context.applicationContext
        val following = followScreen(app) || followBrightness(app)
        if (!RootShell.probablyRooted()) return
        worker.execute { keepLit(app, following) }
        if (!following) return
        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        stopPolling()
                        if (followScreen(ctx)) set(0)
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        if (followScreen(ctx) || followBrightness(ctx)) set(onLevel(ctx))
                        startPolling(ctx)
                    }
                }
            }
        }.also {
            ContextCompat.registerReceiver(
                app, it, IntentFilter().apply { addAction(Intent.ACTION_SCREEN_ON); addAction(Intent.ACTION_SCREEN_OFF) },
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
        lastLevel = -1
        set(onLevel(app))
        startPolling(app)
    }

    /**
     * Following the screen, the light stays lit instead of timing out; the phone's own timeout
     * is kept and put back when neither option is on.
     */
    private fun keepLit(context: Context, following: Boolean) {
        if (route() != Route.Vendor) return
        val prefs = prefs(context)
        val saved = prefs.getInt(KEY_SAVED_TIMEOUT, -1)
        if (following) {
            if (saved < 0) {
                vendorGet(TIMEOUT)?.toIntOrNull()?.let { prefs.edit().putInt(KEY_SAVED_TIMEOUT, it).apply() }
            }
            vendorSet(TIMEOUT, FOLLOW_TIMEOUT_MS)
        } else if (saved >= 0) {
            vendorSet(TIMEOUT, saved)
            prefs.edit().remove(KEY_SAVED_TIMEOUT).apply()
        }
    }

    /** Following the brightness: reads the screen's level, quickly while it's changing. */
    private fun startPolling(context: Context) {
        stopPolling()
        if (!followBrightness(context)) return
        val power = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        var lastShare = -1f
        var fast = 0
        val tick = object : Runnable {
            override fun run() {
                if (power?.isInteractive == false) return
                val share = screenShare(context)
                if (share != lastShare) {
                    lastShare = share
                    fast = 15
                    set(levelForScreen(context))
                }
                if (fast > 0) fast--
                handler.postDelayed(this, if (fast > 0) POLL_MS else IDLE_POLL_MS)
            }
        }
        poller = tick
        handler.post(tick)
    }

    private fun stopPolling() {
        poller?.let { handler.removeCallbacks(it) }
        poller = null
    }

    fun stop(context: Context) {
        val app = context.applicationContext
        receiver?.let { runCatching { app.unregisterReceiver(it) } }
        receiver = null
        stopPolling()
    }

    /** Three quick flashes for a notification, then back to where it was. */
    fun flash(context: Context) {
        if (!notificationFlash(context)) return
        worker.execute {
            val before = brightness() ?: return@execute
            repeat(3) {
                lastLevel = -1; apply(100)
                Thread.sleep(180)
                apply(0)
                Thread.sleep(180)
            }
            lastLevel = -1
            apply(before)
        }
    }
}
