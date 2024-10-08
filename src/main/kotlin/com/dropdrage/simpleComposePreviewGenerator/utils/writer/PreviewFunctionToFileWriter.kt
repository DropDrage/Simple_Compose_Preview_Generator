package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebugResulted
import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.codeinsight.utils.commitAndUnblockDocument
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile

internal object PreviewFunctionToFileWriter {

    private const val WRITE_COMPOSE_PREVIEW_COMMAND = "write_compose_preview"

    private val LOG = thisLogger()

    private val writer: BasePreviewWriter
        get() = PreviewWriterProvider.writer


    fun write(
        project: Project,
        editor: Editor?,
        file: KtFile,
        functionWithPreview: FunctionWithPreview,
        newLine: PsiElement,
        argumentsTemplate: Template,
    ) {
        write(project, editor, file, listOf(functionWithPreview), newLine, false, true) {
            val templateManager = TemplateManager.getInstance(project)
            templateManager.startTemplate(editor!!, argumentsTemplate)
        }
    }

    fun write(
        project: Project,
        editor: Editor?,
        file: KtFile,
        functionWithPreviews: List<FunctionWithPreview>,
        newLine: PsiElement,
    ) {
        write(project, editor, file, functionWithPreviews, newLine, true, false)
    }

    private fun write(
        project: Project,
        editor: Editor?,
        file: KtFile,
        functionWithPreviews: List<FunctionWithPreview>,
        newLine: PsiElement,
        isCommandRequired: Boolean,
        shouldMoveToArgumentsListStart: Boolean,
        afterWriteAction: (() -> Unit)? = null,
    ) {
        val writeLambda: () -> Unit = {
            val shortenReferences = ShortenReferences.DEFAULT
            val codeStyleManager = CodeStyleManager.getInstance(project)

            val argumentsStartPosition = LOG.logTimeOnDebugResulted("Add") {
                writer.addPreviewsToFile(
                    file,
                    functionWithPreviews,
                    newLine,
                    shouldMoveToArgumentsListStart,
                )
            }
            moveCaretAndScroll(editor, argumentsStartPosition)

            LOG.logTimeOnDebug("Commit") { file.commitAndUnblockDocument() }
            LOG.logTimeOnDebug("Shorten") { shortenReferences.process(file) }
            LOG.logTimeOnDebug("Reformat") {
                //                val changedRangesInfo = VcsFacade.getInstance().getChangedRangesInfo(file)
                //                if (changedRangesInfo != null) {
                //                    LOG.debug("Reformat chages")
                //                    codeStyleManager.reformatChanges(file, changedRangesInfo,)
                //                } else {
                //                    LOG.debug("Reformat")
                codeStyleManager.reformat(file)
                //                }
            } // ToDo file or psi?

            afterWriteAction?.invoke()
        }

        if (isCommandRequired) {
            project.executeWriteCommand(WRITE_COMPOSE_PREVIEW_COMMAND, writeLambda)
        } else {
            WriteAction.run<Throwable>(writeLambda)
        }
    }

    private fun moveCaretAndScroll(editor: Editor?, firstElementTextOffset: Int) {
        editor?.apply {
            LOG.debug("Element position: $firstElementTextOffset")
            caretModel.primaryCaret.moveToOffset(firstElementTextOffset)
            scrollingModel.scrollToCaret(ScrollType.CENTER)
        }
    }

}
