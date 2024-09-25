package com.dropdrage.simpleComposePreviewGenerator.intention

import com.dropdrage.simpleComposePreviewGenerator.common.GenerateComposePreviewCommon
import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.createOnlyNewLine
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.isTargetForComposePreview
import com.dropdrage.simpleComposePreviewGenerator.utils.generator.PreviewArgumentsListGenerator
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.modcommand.ModPsiUpdater
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.originalKtFile
import org.jetbrains.kotlin.idea.codeinsight.api.applicable.intentions.AbstractKotlinModCommandWithContext
import org.jetbrains.kotlin.idea.codeinsight.api.applicable.intentions.AnalysisActionContext
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.KotlinApplicabilityRange
import org.jetbrains.kotlin.idea.codeinsight.api.applicators.applicabilityTarget
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Too difficult to make it work.
 * Main problem now is shortening references. Also writer rework required
 */
class GenerateComposePreviewModCommand :
    AbstractKotlinModCommandWithContext<KtNamedFunction, GenerateComposePreviewModCommand.Context>(KtNamedFunction::class) {

    private val shouldGenerateDefaults: Boolean
        get() = ConfigService.config.isDefaultsGenerationEnabled
    private val isSkipViewModel: Boolean
        get() = ConfigService.config.isSkipViewModel
    private val isModifierGenerationEnabled: Boolean
        get() = ConfigService.config.isModifierGenerationEnabled

    private val previewCommon = GenerateComposePreviewCommon()
    private val previewArgumentsListGenerator = PreviewArgumentsListGenerator()


    override fun apply(
        targetFunction: KtNamedFunction,
        context: AnalysisActionContext<Context>,
        updater: ModPsiUpdater,
    ) {
        LOG.logTimeOnDebug("All") {
            val project = context.actionContext.project//targetFunction.project
            val editor = FileEditorManager.getInstance(project).selectedTextEditor

            val previewArgumentsTemplate = previewCommon.buildPreviewFunctionArgumentsTemplate(targetFunction, project)
            val previewFunction = previewCommon.buildPreviewFunctionStringForTemplate(targetFunction, project)
            val preview: KtElement
            val newLine: PsiElement
            LOG.logTimeOnDebug("Psi") {
                val psiFactory = KtPsiFactory(project)
                preview = psiFactory.createFunction(previewFunction)
                newLine = psiFactory.createOnlyNewLine()
            }
            LOG.debug(preview.text)

            val containingKtFile = targetFunction.containingKtFile
            println("Kt: |${containingKtFile.isPhysical}|${containingKtFile.originalFile}|${containingKtFile.originalFile.isPhysical}|${containingKtFile.originalKtFile}|${containingKtFile.virtualFile}|${containingKtFile.fileDocument}|$project")

            LOG.logTimeOnDebug("Write") {
                // ToDo if not whitespace then add it
                // ToDo shorten references doesn't work
                val shortenReferences = ShortenReferences.DEFAULT
                val writable = updater.getWritable(containingKtFile.lastChild)
                writable.add(preview)
                invokeLater(modalityState = ModalityState.defaultModalityState()) {
                    shortenReferences.process(containingKtFile.originalFile as KtFile)
                }
//                PreviewWriter.write(
//                    project,
//                    editor,
//                    containingKtFile,
//                    FunctionWithPreview(targetFunction, preview),
//                    newLine,
//                    previewArgumentsTemplate,
//                )
            }
        }
    }

    override fun getFamilyName(): @IntentionFamilyName String = "Generate Preview"

    override fun getActionName(
        element: KtNamedFunction,
        context: Context,
    ): @IntentionName String = "\\\\ Generate Preview"

    context(KtAnalysisSession)
    override fun prepareContext(element: KtNamedFunction): Context {
        LOG.debug("\\\\ Prepare context")
        val shouldGenerateDefaults = shouldGenerateDefaults
        val isSkipViewModel = isSkipViewModel
        val isModifierGenerationEnabled = isModifierGenerationEnabled
        return Context(
            element.name ?: "Unknown name",
            element.valueParameters.filter {
                previewArgumentsListGenerator.shouldBeParameterAdded(
                    it,
                    shouldGenerateDefaults,
                    isSkipViewModel,
                    isModifierGenerationEnabled
                )
            },
        )
    }

    override fun getApplicabilityRange(): KotlinApplicabilityRange<KtNamedFunction> =
        applicabilityTarget<KtNamedFunction> { it }

    override fun isApplicableByPsi(element: KtNamedFunction): Boolean = element.isTargetForComposePreview()

//    override fun generatePreview(
//        context: ActionContext?,
//        targetFunction: KtNamedFunction?,
//    ): IntentionPreviewInfo {
//        if (context == null || targetFunction == null) return IntentionPreviewInfo.EMPTY
//        LOG.logTimeOnDebug("All") {
//            val project = context.project//targetFunction.project
//            val editor = FileEditorManager.getInstance(project).selectedTextEditor
//
//            val previewArgumentsTemplate = previewCommon.buildPreviewFunctionArgumentsTemplate(targetFunction, project)
//            val previewFunction = previewCommon.buildPreviewFunctionStringForTemplate(targetFunction, project)
//            val preview: KtElement
//            val newLine: PsiElement
//            LOG.logTimeOnDebug("Psi") {
//                val psiFactory = KtPsiFactory(project)
//                preview = psiFactory.createFunction(previewFunction)
//                newLine = psiFactory.createOnlyNewLine()
//            }
//            LOG.debug(preview.text)
//
//            val containingKtFile = targetFunction.containingKtFile //ToDo
//            println("Kt: ${containingKtFile.isPhysical} ${containingKtFile.originalFile} ${containingKtFile.virtualFile} ${containingKtFile.fileDocument} $project")
//            if (containingKtFile.isPhysical) {
//                LOG.logTimeOnDebug("Write") {
//                    PreviewWriter.write(
//                        project,
//                        editor,
//                        containingKtFile,
//                        FunctionWithPreview(targetFunction, preview),
//                        newLine,
//                        previewArgumentsTemplate,
//                    )
//                }
//            } else {
//                LOG.logTimeOnDebug("Write") {
//                    PreviewWriter.write(
//                        project,
//                        editor,
//                        containingKtFile,
//                        FunctionWithPreview(targetFunction, preview),
//                        newLine,
//                        previewArgumentsTemplate,
//                    )
//                }
////                LOG.debug("File is not physical")
//            }
//        }
//        return IntentionPreviewInfo.DIFF
//    }

    data class Context(
        val name: String,
        val arguments: List<KtParameter?>,
    )

    companion object {
        private val LOG = logger<GenerateComposePreviewModCommand>()
    }

}
