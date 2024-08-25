package com.dropdrage.simpleComposePreviewGenerator.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "Simple Compose Preview Generator",
    storages = [Storage("SimpleComposePreviewGenerator.xml")],
    category = SettingsCategory.PLUGINS,
)
class ConfigService : SimplePersistentStateComponent<Config>(Config()) {
    companion object {

        private val ConfigInstance by lazy(LazyThreadSafetyMode.NONE) {
            ApplicationManager.getApplication().getService(ConfigService::class.java)
        }

        val config: Config
            get() = ConfigInstance.state

    }
}
