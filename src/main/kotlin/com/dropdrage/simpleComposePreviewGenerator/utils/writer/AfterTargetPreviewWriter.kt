package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

internal class AfterTargetPreviewWriter : BasePreviewWriter(LOG, false) {

    override fun KtFile.addPreview(target: KtElement, preview: KtElement): PsiElement = addAfter(preview, target)


    companion object {
        private val LOG = logger<AfterTargetPreviewWriter>()
    }

}
