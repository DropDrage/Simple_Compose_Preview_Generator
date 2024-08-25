package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal class EndFilePsiElementsWriter : BasePsiElementsWriter() {
    override fun addElementsToFile(
        file: KtFile,
        psiElements: List<FunctionWithPreview>,
        newLine: PsiElement,
    ): Int {
        val firstElementOffset: Int =
            if (psiElements.isNotEmpty()) {
                val (target, preview) = psiElements.first()
                file.add(preview).getFirstFunctionArgumentValueOffset(target)
            } else error("Preview elements required")

        for (i in 1 until psiElements.size) {
            file.add(psiElements[i].preview)
        }
        file.add(newLine)

        return firstElementOffset
    }
}
