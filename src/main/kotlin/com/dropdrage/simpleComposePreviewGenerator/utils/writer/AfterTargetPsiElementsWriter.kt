package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal class AfterTargetPsiElementsWriter : BasePsiElementsWriter(LOG, true) {

    override fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement =
        addAfter(functionWithPreview.preview, functionWithPreview.target)


    companion object {
        private val LOG = logger<AfterTargetPsiElementsWriter>()
    }

}
