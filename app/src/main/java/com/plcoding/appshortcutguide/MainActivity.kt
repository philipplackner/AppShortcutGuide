package com.plcoding.appshortcutguide

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.plcoding.appshortcutguide.ui.theme.AppShortcutGuideTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            AppShortcutGuideTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        16.dp, Alignment.CenterVertically
                    )
                ) {
                    when(viewModel.shortcutType) {
                        ShortcutType.STATIC -> Text("Static shortcut clicked")
                        ShortcutType.DYNAMIC -> Text("Dynamic shortcut clicked")
                        ShortcutType.PINNED -> Text("Pinned shortcut clicked")
                        null -> Unit
                    }
                    Button(
                        onClick = ::addDynamicShortcut
                    ) {
                        Text("Add dynamic shortcut")
                    }
                    Button(
                        onClick = ::addPinnedShortcut
                    ) {
                        Text("Add pinned shortcut")
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun addPinnedShortcut() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val shortcutManager = getSystemService<ShortcutManager>()!!
        if(shortcutManager.isRequestPinShortcutSupported) {
            val shortcut = ShortcutInfo.Builder(applicationContext, "pinned")
                .setShortLabel("Send message")
                .setLongLabel("This sends a message to a friend")
                .setIcon(Icon.createWithResource(
                    applicationContext, R.drawable.baseline_baby_changing_station_24
                ))
                .setIntent(
                    Intent(applicationContext, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra("shortcut_id", "pinned")
                    }
                )
                .build()

            val callbackIntent = shortcutManager.createShortcutResultIntent(shortcut)
            val successPendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                0,
                callbackIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            shortcutManager.requestPinShortcut(shortcut, successPendingIntent.intentSender)
        }
    }

    private fun addDynamicShortcut() {
        val shortcut = ShortcutInfoCompat.Builder(applicationContext, "dynamic")
            .setShortLabel("Call Mum")
            .setLongLabel("Clicking this will call your mum")
            .setIcon(IconCompat.createWithResource(
                applicationContext, R.drawable.baseline_baby_changing_station_24
            ))
            .setIntent(
                Intent(applicationContext, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    putExtra("shortcut_id", "dynamic")
                }
            )
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(applicationContext, shortcut)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            when(intent.getStringExtra("shortcut_id")) {
                "static" -> viewModel.onShortcutClicked(ShortcutType.STATIC)
                "dynamic" -> viewModel.onShortcutClicked(ShortcutType.DYNAMIC)
                "pinned" -> viewModel.onShortcutClicked(ShortcutType.PINNED)
            }
        }
    }
}