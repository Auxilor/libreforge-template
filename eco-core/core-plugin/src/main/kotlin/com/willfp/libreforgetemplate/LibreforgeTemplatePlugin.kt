package com.willfp.libreforgetemplate

import com.willfp.libreforge.LibReforgePlugin
import com.willfp.libreforgetemplate.bosses.bossHolders

lateinit var plugin: LibreforgeTemplatePlugin
    private set

class LibreforgeTemplatePlugin : LibReforgePlugin() {
    init {
        plugin = this
        registerHolderProvider { it.bossHolders }
    }
}
