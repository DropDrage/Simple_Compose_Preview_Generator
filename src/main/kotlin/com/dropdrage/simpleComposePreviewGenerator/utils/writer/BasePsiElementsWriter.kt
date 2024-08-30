package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.resolveReferencedPsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import kotlin.time.measureTime

internal abstract class BasePsiElementsWriter(private val isNewLineAfterAllPreviewsIgnored: Boolean) {

    fun addElementsToFile(
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
        if (isNewLineAfterAllPreviewsIgnored) {
            file.add(newLine)
        }

        return firstElementOffset
    }


    private fun PsiElement.getFirstArgumentValueOffset(target: PsiElement): Int {
        val child: PsiElement
        val time = measureTime {
            child = findTargetCallElement(target).findDescendantOfType<KtValueArgument>()!!.lastChild
        }
        println("Child time: $time")
        return child.textOffset
    }

    private fun PsiElement.getArgumentsListStartOffset(target: PsiElement): Int {
        val child: PsiElement
        val time = measureTime {
            child = findTargetCallElement(target).lastChild.firstChild.nextSibling
        }
        println("Child time: $time")
        return child.textOffset
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun PsiElement.findTargetCallElement(target: PsiElement) =
        findDescendantOfType<KtCallExpression> { it.resolveReferencedPsiElement == target }!!


    protected abstract fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement

}
