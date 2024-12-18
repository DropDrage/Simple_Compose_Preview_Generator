/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "Simple Compose Preview Generator",
    storages = [Storage("SimpleComposePreviewGenerator.xml", exportable = true)],
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
