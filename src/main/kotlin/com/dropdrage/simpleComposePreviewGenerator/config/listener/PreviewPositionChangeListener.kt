/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.config.listener

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic

interface PreviewPositionChangeListener {

    fun onPreviewPositionChanged()

    companion object {
        val TOPIC = Topic.create(
            "PreviewPositionChanged",
            PreviewPositionChangeListener::class.java,
        )

        fun subscribe(listener: PreviewPositionChangeListener) {
            ApplicationManager.getApplication().messageBus.connect().subscribe(TOPIC, listener)
        }
    }

}

val PreviewPositionChangePublisher =
    ApplicationManager.getApplication().messageBus.syncPublisher(PreviewPositionChangeListener.TOPIC)