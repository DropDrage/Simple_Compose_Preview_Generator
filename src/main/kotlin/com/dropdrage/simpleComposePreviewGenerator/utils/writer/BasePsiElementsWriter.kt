package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.resolveReferencedPsiElement
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import kotlin.time.measureTime

internal abstract class BasePsiElementsWriter {

    abstract fun addElementsToFile(
        file: KtFile,
        psiElements: List<FunctionWithPreview>,
        newLine: PsiElement,
    ): Int

    protected fun PsiElement.getFirstFunctionArgumentValueOffset(target: PsiElement): Int {
        val child: PsiElement
        val time = measureTime {
            child = findDescendantOfType<KtCallExpression> { it.resolveReferencedPsiElement == target }!!
                .findDescendantOfType<KtValueArgument>()!!
                .lastChild
        }
        println("Child time: $time")
        return child.textOffset
    }

}
