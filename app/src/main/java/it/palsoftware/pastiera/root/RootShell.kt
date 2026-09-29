package it.palsoftware.pastiera.root

import android.util.Log
import java.util.concurrent.TimeUnit

/**
 * Root (su) for the Root page's features. Whether the phone is rooted is asked once, quietly:
 * a phone without su never sees the page. Commands run one at a time, off the main thread.
 */
object RootShell {
    private const val TAG = "FluxRoot"
    @Volatile private var checked: Boolean? = null

    /** Whether su is there and grants root (asked once; the first ask may show the root prompt). */
    fun available(): Boolean {
        checked?.let { return it }
        val suExists = listOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/debug_ramdisk/su", "/data/adb/ksu/bin/su")
            .any { java.io.File(it).exists() } || runCatching {
                Runtime.getRuntime().exec(arrayOf("which", "su")).inputStream.bufferedReader().readText().isNotBlank()
            }.getOrDefault(false)
        if (!suExists) {
            checked = false
            return false
        }
        val granted = run("id")?.contains("uid=0") == true
        checked = granted
        return granted
    }

    /** Whether su exists, without asking for root (for showing the page before the first ask). */
    fun probablyRooted(): Boolean = checked ?: listOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/debug_ramdisk/su", "/data/adb/ksu/bin/su")
        .any { java.io.File(it).exists() }

    /** Runs [command] as root; its output, or null when it failed or took over [timeoutMs]. */
    fun run(command: String, timeoutMs: Long = 4_000): String? = runCatching {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        val finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) {
            process.destroy()
            return null
        }
        val out = process.inputStream.bufferedReader().readText()
        if (process.exitValue() != 0) {
            Log.w(TAG, "root command failed (${process.exitValue()}): ${process.errorStream.bufferedReader().readText().take(200)}")
            null
        } else out
    }.getOrNull()

    /** A long-running root process (getevent), for reading the keyboard's touch pad. */
    fun start(command: String): Process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))

    /** Runs [command] as root off the main thread. */
    fun runAsync(command: String, onDone: (String?) -> Unit = {}) {
        Thread { onDone(run(command)) }.start()
    }
}
