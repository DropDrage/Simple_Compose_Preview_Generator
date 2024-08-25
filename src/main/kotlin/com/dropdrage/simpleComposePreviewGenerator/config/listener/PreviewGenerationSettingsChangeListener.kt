package com.dropdrage.simpleComposePreviewGenerator.config.listener

import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.Topic

interface PreviewGenerationSettingsChangeListener {

    fun onChanged()

    companion object {
        val TOPIC = Topic.create(
            "PreviewGenerationSettingsChanged",
            PreviewGenerationSettingsChangeListener::class.java,
        )
    }

}

val PreviewGenerationSettingsChangePublisher =
    ApplicationManager.getApplication().messageBus.syncPublisher(PreviewGenerationSettingsChangeListener.TOPIC)
