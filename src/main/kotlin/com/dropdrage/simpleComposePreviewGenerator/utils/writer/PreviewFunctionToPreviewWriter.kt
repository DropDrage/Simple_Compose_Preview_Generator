/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal object PreviewFunctionToPreviewWriter {

    private val LOG = thisLogger()

    private val writer: BasePreviewWriter
        get() = PreviewWriterProvider.writer


    fun write(file: KtFile, functionWithPreview: FunctionWithPreview, newLine: PsiElement) {
        LOG.logTimeOnDebug("Preview/Add") {
            writer.addPreviewToFileWithoutOffsetReturn(file, functionWithPreview, newLine)
        }
    }

}
