package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal class AfterTargetPsiElementsWriter : BasePsiElementsWriter(true) {
    override fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement =
        addAfter(functionWithPreview.preview, functionWithPreview.target)
}
