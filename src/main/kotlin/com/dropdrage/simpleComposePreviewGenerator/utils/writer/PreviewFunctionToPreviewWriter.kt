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
