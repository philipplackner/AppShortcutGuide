package com.plcoding.appshortcutguide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    var shortcutType by mutableStateOf<ShortcutType?>(null)
        private set

    fun onShortcutClicked(type: ShortcutType) {
        shortcutType = type
    }
}

enum class ShortcutType {
    STATIC, DYNAMIC, PINNED
}