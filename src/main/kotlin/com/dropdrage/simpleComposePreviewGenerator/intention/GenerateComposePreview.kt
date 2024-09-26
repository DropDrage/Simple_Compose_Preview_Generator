package com.dropdrage.simpleComposePreviewGenerator.intention

import com.dropdrage.simpleComposePreviewGenerator.common.GenerateComposePreviewCommon
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.createOnlyNewLine
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.isTargetForComposePreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.FunctionWithPreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.PreviewFunctionToFileWriter
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.PreviewFunctionToPreviewWriter
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.codeinsight.api.classic.intentions.SelfTargetingOffsetIndependentIntention
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory

internal class GenerateComposePreview : SelfTargetingOffsetIndependentIntention<KtNamedFunction>(
    KtNamedFunction::class.java,
    { "|||| Generate Preview" },
    { "Generate Preview" },
) {

    private val previewCommon = GenerateComposePreviewCommon()


    override fun isApplicableTo(element: KtNamedFunction): Boolean = element.isTargetForComposePreview()

    override fun generatePreview(project: Project, editor: Editor, file: PsiFile): IntentionPreviewInfo {
        val targetFunction = getTarget(editor, file) ?: return IntentionPreviewInfo.EMPTY
        LOG.logTimeOnDebug("Preview/All") {
            val previewString = previewCommon.buildPreviewStringOfPreviewFunction(targetFunction, project)
            val previewFunction: KtElement
            val newLine: PsiElement
            LOG.logTimeOnDebug("Preview/Psi") {
                val project = targetFunction.project
                val psiFactory = KtPsiFactory(project)
                previewFunction = psiFactory.createFunction(previewString)
                newLine = psiFactory.createOnlyNewLine()
            }
            LOG.debug(lazyMessage = { previewFunction.text })

            val containingKtFile = targetFunction.containingKtFile
            PreviewFunctionToPreviewWriter.write(
                containingKtFile,
                FunctionWithPreview(targetFunction, previewFunction),
                newLine,
            )
        }
        return IntentionPreviewInfo.DIFF
    }

    override fun applyTo(targetFunction: KtNamedFunction, editor: Editor?) = LOG.logTimeOnDebug("All") {
        val containingKtFile = targetFunction.containingKtFile
        if (!containingKtFile.isPhysical) return@logTimeOnDebug

        val project = targetFunction.project
        val previewArgumentsTemplate = previewCommon.buildPreviewFunctionArgumentsTemplate(targetFunction, project)
        val previewString = previewCommon.buildPreviewFunctionStringForTemplate(targetFunction, project)
        val previewFunction: KtElement
        val newLine: PsiElement
        LOG.logTimeOnDebug("Psi") {
            val psiFactory = KtPsiFactory(project)
            previewFunction = psiFactory.createFunction(previewString)
            newLine = psiFactory.createOnlyNewLine()
        }
        LOG.debug(lazyMessage = { previewFunction.text })

        LOG.logTimeOnDebug("Write") {
            PreviewFunctionToFileWriter.write(
                project,
                editor,
                containingKtFile,
                FunctionWithPreview(targetFunction, previewFunction),
                newLine,
                previewArgumentsTemplate,
            )
        }
    }


    companion object {
        private val LOG = logger<GenerateComposePreview>()
    }

}
