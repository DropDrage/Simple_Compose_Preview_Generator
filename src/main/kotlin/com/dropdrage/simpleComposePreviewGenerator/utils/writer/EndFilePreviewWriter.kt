package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

internal class EndFilePreviewWriter : BasePreviewWriter(LOG, true) {

    override fun KtFile.addPreview(target: KtElement, preview: KtElement): PsiElement = add(preview)


    companion object {
        private val LOG = logger<EndFilePreviewWriter>()
    }

}
