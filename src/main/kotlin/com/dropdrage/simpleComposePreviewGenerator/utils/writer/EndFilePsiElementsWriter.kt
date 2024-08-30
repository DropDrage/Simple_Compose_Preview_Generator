package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal class EndFilePsiElementsWriter : BasePsiElementsWriter(false) {
    override fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement =
        add(functionWithPreview.preview)
}
