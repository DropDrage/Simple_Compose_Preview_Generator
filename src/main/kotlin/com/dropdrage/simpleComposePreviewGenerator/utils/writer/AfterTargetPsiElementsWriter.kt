package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtFile

internal class AfterTargetPsiElementsWriter : BasePsiElementsWriter() {
    override fun addElementsToFile(
        file: KtFile,
        psiElements: List<FunctionWithPreview>,
        newLine: PsiElement,
    ): Int {
        val firstElementOffset: Int =
            if (psiElements.isNotEmpty()) {
                val (target, psiElement) = psiElements.first()
                file.addAfter(psiElement, target).getFirstFunctionArgumentValueOffset(target)
            } else error("Preview elements required")

        for (i in 1 until psiElements.size) {
            val (targetFunction, previewFunction) = psiElements[i]
            file.addAfter(previewFunction, targetFunction)
        }

        return firstElementOffset
    }
}
