/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebugResulted
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.resolveReferencedPsiElement
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

internal abstract class BasePreviewWriter(
    private val LOG: Logger,
    private val isNewLineAfterAllPreviewsRequired: Boolean,
) {

    fun addPreviewToFileWithoutOffsetReturn(
        file: KtFile,
        functionWithPreview: FunctionWithPreview,
        newLine: PsiElement,
    ) {
        file.addPreview(functionWithPreview)

        if (isNewLineAfterAllPreviewsRequired) {
            file.add(newLine)
        }
    }

    /**
     * @return if [isArgumentsListStartOffsetRequired] arguments list start offset,
     * otherwise - first argument value offset
     */
    fun addPreviewsToFile(
        file: KtFile,
        functionWithPreviews: List<FunctionWithPreview>,
        newLine: PsiElement,
        isArgumentsListStartOffsetRequired: Boolean,
    ): Int {
        if (functionWithPreviews.isEmpty()) error("Preview elements required")

        val firstPreview = functionWithPreviews.first()
        val argumentedPreview = file.addPreview(firstPreview.target, firstPreview.previewWithForcedArguments)
        val firstElementOffset =
            if (isArgumentsListStartOffsetRequired) argumentedPreview.getArgumentsListStartOffset(firstPreview.target)
            else argumentedPreview.getFirstArgumentValueOffset(firstPreview.target)
        argumentedPreview.delete()

        for (functionWithPreview in functionWithPreviews) {
            file.addPreview(functionWithPreview)
        }
        if (isNewLineAfterAllPreviewsRequired) {
            file.add(newLine)
        }

        return firstElementOffset
    }


    private fun PsiElement.getFirstArgumentValueOffset(target: PsiElement): Int = LOG.logTimeOnDebugResulted("Child") {
        findTargetCallElement(target).findDescendantOfType<KtValueArgument>()!!.lastChild.textOffset
    }

    private fun PsiElement.getArgumentsListStartOffset(target: KtElement): Int = LOG.logTimeOnDebugResulted("Child") {
        findTargetCallElement(target).lastChild.firstChild.nextSibling.textOffset
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun PsiElement.findTargetCallElement(target: PsiElement) =
        findDescendantOfType<KtCallExpression> { it.resolveReferencedPsiElement == target }!!


    protected fun KtFile.addPreview(functionWithPreview: FunctionWithPreview): PsiElement =
        addPreview(functionWithPreview.target, functionWithPreview.preview)

    protected abstract fun KtFile.addPreview(target: KtElement, preview: KtElement): PsiElement

}
