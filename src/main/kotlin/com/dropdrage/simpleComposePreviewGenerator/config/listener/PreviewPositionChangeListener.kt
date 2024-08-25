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