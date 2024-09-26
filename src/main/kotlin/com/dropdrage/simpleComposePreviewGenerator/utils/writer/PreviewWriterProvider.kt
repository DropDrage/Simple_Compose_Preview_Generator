package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewLocation
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewPositionChangeListener

internal object PreviewWriterProvider : PreviewPositionChangeListener {

    var writer: BasePreviewWriter
        private set
    private var currentWriterType: PreviewLocation = ConfigService.config.previewLocation


    init {
        writer = when (currentWriterType) {
            PreviewLocation.FILE_END -> EndFilePreviewWriter()
            PreviewLocation.AFTER_FUNCTION -> AfterTargetPreviewWriter()
        }
        PreviewPositionChangeListener.subscribe(this)
    }


    override fun onPreviewPositionChanged() {
        val previewLocation = ConfigService.config.previewLocation
        if (currentWriterType != previewLocation) {
            writer = when (previewLocation) {
                PreviewLocation.FILE_END -> EndFilePreviewWriter()
                PreviewLocation.AFTER_FUNCTION -> AfterTargetPreviewWriter()
            }
        }
    }

}
