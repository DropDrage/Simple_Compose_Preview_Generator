package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal class EndFilePsiElementsWriter : BasePsiElementsWriter(LOG, false) {

    override fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement =
        add(functionWithPreview.preview)


    companion object {
        private val LOG = logger<EndFilePsiElementsWriter>()
    }

}
