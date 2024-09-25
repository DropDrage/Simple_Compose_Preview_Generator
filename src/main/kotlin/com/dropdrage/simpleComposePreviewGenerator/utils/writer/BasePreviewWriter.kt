package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.resolveReferencedPsiElement
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

internal abstract class BasePreviewWriter(
    private val LOG: Logger,
    private val isNewLineAfterAllPreviewsRequired: Boolean,
) {

    fun addPreviewsToFile(
        file: KtFile,
        psiElements: List<FunctionWithPreview>,
        newLine: PsiElement,
        shouldMoveToArgumentsListStart: Boolean,
    ): Int {
        val firstElementOffset: Int =
            if (psiElements.isNotEmpty()) {
                val firstPreview = psiElements.first()
                val addedPreview = file.addPreview(firstPreview)
                if (shouldMoveToArgumentsListStart) {
                    addedPreview.getArgumentsListStartOffset(firstPreview.target)
                } else {
                    addedPreview.getFirstArgumentValueOffset(firstPreview.target)
                }
            } else error("Preview elements required")

        for (i in 1 until psiElements.size) {
            file.addPreview(psiElements[i])
        }
        if (isNewLineAfterAllPreviewsRequired) {
            file.add(newLine)
        }

        return firstElementOffset
    }


    private fun PsiElement.getFirstArgumentValueOffset(target: PsiElement): Int {
        val child: PsiElement
        LOG.logTimeOnDebug("Child") {
            child = findTargetCallElement(target).findDescendantOfType<KtValueArgument>()!!.lastChild
        }
        return child.textOffset
    }

    private fun PsiElement.getArgumentsListStartOffset(target: PsiElement): Int {
        val child: PsiElement
        LOG.logTimeOnDebug("Child") {
            child = findTargetCallElement(target).lastChild.firstChild.nextSibling
        }
        return child.textOffset
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun PsiElement.findTargetCallElement(target: PsiElement) =
        findDescendantOfType<KtCallExpression> { it.resolveReferencedPsiElement == target }!!


    protected abstract fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement

}
