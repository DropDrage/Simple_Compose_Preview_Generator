package com.dropdrage.simpleComposePreviewGenerator.intention

import com.dropdrage.simpleComposePreviewGenerator.common.GenerateComposePreviewCommon
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.createOnlyNewLine
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.isTargetForComposePreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.FunctionWithPreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.PreviewWriter
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
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

    override fun applyTo(targetFunction: KtNamedFunction, editor: Editor?) = LOG.logTimeOnDebug("All") {
        val containingKtFile = targetFunction.containingKtFile
        if (!containingKtFile.isPhysical) return@logTimeOnDebug

        val project = targetFunction.project
        val previewArgumentsTemplate = previewCommon.buildPreviewFunctionArgumentsTemplate(targetFunction, project)
        val previewFunction = previewCommon.buildPreviewFunctionStringForTemplate(targetFunction, project)
        val preview: KtElement
        val newLine: PsiElement
        LOG.logTimeOnDebug("Psi") {
            val psiFactory = KtPsiFactory(project)
            preview = psiFactory.createFunction(previewFunction)
            newLine = psiFactory.createOnlyNewLine()
        }
        LOG.debug(lazyMessage = { preview.text })

        LOG.logTimeOnDebug("Write") {
            PreviewWriter.write(
                project,
                editor,
                containingKtFile,
                FunctionWithPreview(targetFunction, preview),
                newLine,
                previewArgumentsTemplate,
            )
        }
    }


    companion object {
        private val LOG = logger<GenerateComposePreview>()
    }

}
