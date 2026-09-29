package it.palsoftware.pastiera

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import it.palsoftware.pastiera.shortcuts.UserShortcut
import it.palsoftware.pastiera.shortcuts.UserShortcuts
import it.palsoftware.pastiera.ui.theme.PastieraTheme

/**
 * Add a shortcut: apps that offer shortcuts for the home screen (a contact's direct dial, a
 * bookmark, a settings page) hand one over here, and it becomes a quick launcher result.
 */
class UserShortcutsActivity : LocalizedComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PastieraTheme {
                UserShortcutsScreen(onBack = { finish() })
            }
        }
    }
}

private data class ShortcutProvider(val component: ComponentName, val label: String, val icon: Drawable?)

@Composable
private fun UserShortcutsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var shortcuts by remember { mutableStateOf(UserShortcuts.all(context)) }
    val providers = remember {
        val pm = context.packageManager
        pm.queryIntentActivities(Intent(Intent.ACTION_CREATE_SHORTCUT), 0)
            .filter { it.activityInfo.exported && it.activityInfo.packageName != context.packageName }
            .map {
                ShortcutProvider(
                    ComponentName(it.activityInfo.packageName, it.activityInfo.name),
                    it.loadLabel(pm).toString(),
                    runCatching { it.loadIcon(pm) }.getOrNull()
                )
            }
            .sortedBy { it.label.lowercase() }
    }
    var pendingPackage by rememberSaveable { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val packageName = pendingPackage ?: return@rememberLauncherForActivityResult
        pendingPackage = null
        if (result.resultCode != android.app.Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val added = UserShortcuts.addFromResult(context, packageName, result.data)
        if (added == null) {
            Toast.makeText(context, R.string.user_shortcuts_unsupported, Toast.LENGTH_LONG).show()
        } else {
            shortcuts = UserShortcuts.all(context)
            Toast.makeText(context, context.getString(R.string.user_shortcuts_added, added.label), Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {
        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 1.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.settings_back_content_description))
                }
                Text(
                    stringResource(R.string.user_shortcuts_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        LazyColumn(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars)) {
            item {
                Text(
                    stringResource(R.string.user_shortcuts_intro),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (shortcuts.isNotEmpty()) {
                item { SectionLabel(stringResource(R.string.user_shortcuts_yours)) }
                items(shortcuts, key = { it.id }) { shortcut ->
                    AddedShortcutRow(shortcut) {
                        UserShortcuts.remove(context, shortcut.id)
                        shortcuts = UserShortcuts.all(context)
                    }
                }
            }
            item { SectionLabel(stringResource(R.string.user_shortcuts_add_from)) }
            if (providers.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.user_shortcuts_none),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(providers, key = { it.component.flattenToString() }) { provider ->
                ShortcutRow(icon = provider.icon, label = provider.label, onClick = {
                    pendingPackage = provider.component.packageName
                    runCatching {
                        picker.launch(Intent(Intent.ACTION_CREATE_SHORTCUT).setComponent(provider.component))
                    }.onFailure {
                        pendingPackage = null
                        Toast.makeText(context, R.string.user_shortcuts_unsupported, Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
private fun AddedShortcutRow(shortcut: UserShortcut, onRemove: () -> Unit) {
    val context = LocalContext.current
    val icon = remember(shortcut.id) { UserShortcuts.icon(context, shortcut) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShortcutIcon(icon)
        Spacer(Modifier.width(16.dp))
        Text(shortcut.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.user_shortcuts_remove, shortcut.label))
        }
    }
}

@Composable
private fun ShortcutRow(icon: Drawable?, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ShortcutIcon(icon)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ShortcutIcon(icon: Drawable?) {
    val bitmap = remember(icon) { icon?.let { runCatching { it.toBitmap(96, 96).asImageBitmap() }.getOrNull() } }
    Box(modifier = Modifier.size(36.dp)) {
        if (bitmap != null) Image(bitmap = bitmap, contentDescription = null, modifier = Modifier.size(36.dp))
    }
}
