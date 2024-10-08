package com.dropdrage.simpleComposePreviewGenerator.action

import com.dropdrage.simpleComposePreviewGenerator.common.GenerateComposePreviewCommon
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebugResulted
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.createOnlyNewLine
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.isComposePreviewFunction
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.isTargetForComposePreview
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.resolveReferencedPsiElement
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.FunctionWithPreview
import com.dropdrage.simpleComposePreviewGenerator.utils.writer.PreviewFunctionToFileWriter
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.psi.util.childrenOfType
import org.jetbrains.kotlin.idea.core.script.configuration.utils.getKtFile
import org.jetbrains.kotlin.idea.refactoring.hostEditor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

private typealias PreviewFunctions = List<KtNamedFunction>

@Suppress("NOTHING_TO_INLINE")
internal class GenerateAllComposePreview : AnAction() {

    private val previewCommon = GenerateComposePreviewCommon()


    override fun actionPerformed(e: AnActionEvent) {
        LOG.logTimeOnDebug("/////////////// All") {
            val project = e.project
            val hostEditor = e.dataContext.hostEditor
            val ktFile = project?.getKtFile(hostEditor?.virtualFile) ?: error("No file found")

            val psiFactory = KtPsiFactory(project)
            val composePreviews = LOG.logTimeOnDebugResulted("/////////////// Compose") {
                buildList {
                    val functions = ktFile.childrenOfType<KtNamedFunction>()
                    for (function in functions) {
                        if (function.isTargetForComposePreviewWithoutPreview(functions)) {
                            val previewFunction = previewCommon.buildPreviewFunctionString(function, project)

                            val previewPsi = LOG.logTimeOnDebugResulted("/////////////// Psi") {
                                psiFactory.createFunction(previewFunction)
                            }
                            add(FunctionWithPreview(function, previewPsi))
                        }
                    }
                }
            }
            val newLine = psiFactory.createOnlyNewLine()

            val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
            LOG.logTimeOnDebug("/////////////// Write") {
                PreviewFunctionToFileWriter.write(project, editor, ktFile, composePreviews, newLine)
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val functions = LOG.logTimeOnDebugResulted("Update") {
            e.project?.getKtFile(e.dataContext.hostEditor?.virtualFile)?.childrenOfType<KtNamedFunction>()
        }
        e.presentation.isEnabledAndVisible = functions?.any {
            it.isTargetForComposePreviewWithoutPreview(functions)
        } == true
    }

    private inline fun KtNamedFunction.isTargetForComposePreviewWithoutPreview(functions: PreviewFunctions): Boolean =
        isTargetForComposePreview() && !hasPreviewIn(functions)

    private fun KtNamedFunction.hasPreviewIn(functions: PreviewFunctions): Boolean =
        functions.any { function ->
            function.isComposePreviewFunction() &&
                function.anyDescendantOfType<KtCallExpression> { call ->
                    call.resolveReferencedPsiElement == this
                }
        }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT


    companion object {
        private val LOG = logger<GenerateAllComposePreview>()
    }

}
