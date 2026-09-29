package it.palsoftware.pastiera.commands

import android.content.Context
import it.palsoftware.pastiera.R
import it.palsoftware.pastiera.root.RootShell

/**
 * Root-only commands, for keys (Customize keys) and the quick launcher: force-stop the app in
 * front, restart the system UI, Battery Saver on or off, and the app in front offline.
 * Only on rooted phones.
 */
class RootCommandSource : CommandSource {
    override val id = CommandSourceId.Pastiera

    override fun getCommands(context: Context): List<CommandTarget> {
        if (!RootShell.probablyRooted()) return emptyList()
        fun command(action: String, label: Int, subtitle: Int, tokens: List<String>) = CommandTarget(
            id = "root.$action",
            source = id,
            kind = CommandKind.PastieraAction,
            label = context.getString(label),
            subtitle = context.getString(subtitle),
            icon = CommandIcon.Settings,
            launch = CommandLaunchSpec.InternalAction("root_$action"),
            capabilities = setOf(CommandCapability.AdjustsDeviceState),
            defaultSurfaces = setOf(CommandSurface.AssignedKey, CommandSurface.QuickLauncher, CommandSurface.NavMode),
            searchTokens = tokens + "root"
        )
        return listOf(
            command(FORCE_STOP, R.string.root_force_stop_title, R.string.root_command_subtitle, listOf("Force stop", "Kill", "Close app")),
            command(RESTART_SYSTEM_UI, R.string.root_restart_systemui_title, R.string.root_command_subtitle, listOf("Restart", "System UI")),
            command(BATTERY_SAVER, R.string.root_battery_saver_title, R.string.root_command_subtitle, listOf("Battery", "Saver", "Power")),
            command(BLOCK_NETWORK, R.string.root_block_network_title, R.string.root_command_subtitle, listOf("Network", "Offline", "Block", "Internet"))
        )
    }

    companion object {
        const val FORCE_STOP = "force_stop"
        const val RESTART_SYSTEM_UI = "restart_systemui"
        const val BATTERY_SAVER = "battery_saver"
        const val BLOCK_NETWORK = "block_network"

        /** Runs a root command's action; false when it isn't one. */
        fun execute(context: Context, action: String): Boolean {
            val front = it.palsoftware.pastiera.inputmethod.QuickLauncherOpener.foregroundPackage
            when (action.removePrefix("root_")) {
                FORCE_STOP -> front?.takeIf { it != context.packageName }?.let { RootShell.runAsync("am force-stop $it") }
                RESTART_SYSTEM_UI -> RootShell.runAsync("pkill -f com.android.systemui")
                BATTERY_SAVER -> RootShell.runAsync(
                    "if [ \"$(settings get global low_power)\" = \"1\" ]; then cmd power set-mode 0; else cmd power set-mode 1; fi"
                )
                // Cuts the app in front off the network (both ways), or lets it back on
                BLOCK_NETWORK -> front?.let { pkg ->
                    RootShell.runAsync(
                        "uid=$(cmd package list packages -U $pkg | grep -o 'uid:[0-9]*' | head -1 | cut -d: -f2); " +
                            "[ -n \"\$uid\" ] && (iptables -C OUTPUT -m owner --uid-owner \$uid -j REJECT 2>/dev/null " +
                            "&& iptables -D OUTPUT -m owner --uid-owner \$uid -j REJECT " +
                            "|| iptables -I OUTPUT -m owner --uid-owner \$uid -j REJECT)"
                    )
                }
                else -> return false
            }
            return true
        }
    }
}
