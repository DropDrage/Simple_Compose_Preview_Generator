/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
