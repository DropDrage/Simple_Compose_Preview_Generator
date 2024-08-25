package com.dropdrage.simpleComposePreviewGenerator.intention

import com.dropdrage.simpleComposePreviewGenerator.common.ComposePreviewCommon
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.createOnlyNewLine
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.isTargetForComposePreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.FunctionWithPreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.PreviewWriter
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.toml.lang.psi.ext.elementType
import kotlin.time.measureTime

internal class GenerateComposePreview : PsiElementBaseIntentionAction() {

    private val previewCommon = ComposePreviewCommon()


    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        val isComposable: Boolean
        val time = measureTime {
            isComposable = getFunctionOrNull(element)?.isTargetForComposePreview() ?: return false
        }
        println("//// isComposable: $isComposable $time")
        return isComposable
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val allTime = measureTime {
            val functionElement = getFunctionOrNull(element) ?: error("${element.text} is not a Kotlin function")

            val previewFunction = previewCommon.buildPreviewFunctionString(functionElement, project)
            val previewPsi: PsiElement
            val newLine: PsiElement
            val psiTime = measureTime {
                val psiFactory = KtPsiFactory(project)
                previewPsi = psiFactory.createFunction(previewFunction)
                newLine = psiFactory.createOnlyNewLine()
            }
            println("Psi time: $psiTime")

            val containingKtFile = functionElement.containingKtFile
            if (containingKtFile.isPhysical) {
                val writeTime = measureTime {
                    PreviewWriter.write(
                        project,
                        editor,
                        containingKtFile,
                        FunctionWithPreview(functionElement, previewPsi),
                        newLine,
                    )
                }
                println("Write time: $writeTime")
            }
        }
        println("All time: $allTime")
    }

    private fun getFunctionOrNull(psiElement: PsiElement): KtNamedFunction? =
        if (psiElement is KtNamedFunction) psiElement
        else {
            val parent = psiElement.parent
            val elementType = psiElement.elementType

            if (elementType == KtTokens.IDENTIFIER && parent is KtNamedFunction) parent
            else getFunctionOnParenthesisOrNull(elementType, parent)
        }

    private fun getFunctionOnParenthesisOrNull(
        elementType: IElementType,
        parent: PsiElement,
    ): KtNamedFunction? = if (elementType == KtTokens.LPAR) {
        val prevSibling = parent.parent
        if (prevSibling is KtNamedFunction) prevSibling
        else prevSibling.parent as? KtNamedFunction
    } else null


    override fun getFamilyName(): String = text

    override fun getText(): String = "//////// Generate Compose preview"

}
