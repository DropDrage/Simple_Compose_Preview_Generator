package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewLocation
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewPositionChangeListener
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.idea.codeinsight.utils.commitAndUnblockDocument
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile
import kotlin.time.measureTime

internal object PreviewWriter : PreviewPositionChangeListener {

    private const val WRITE_COMPOSE_PREVIEW_COMMAND = "write_compose_preview"

    private var currentWriterType: PreviewLocation = ConfigService.config.previewLocation
    private var writer: BasePsiElementsWriter


    init {
        writer = when (currentWriterType) {
            PreviewLocation.FILE_END -> EndFilePsiElementsWriter()
            PreviewLocation.AFTER_FUNCTION -> AfterTargetPsiElementsWriter()
        }
        PreviewPositionChangeListener.subscribe(this)
    }

    override fun onPreviewPositionChanged() {
        val previewLocation = ConfigService.config.previewLocation
        if (currentWriterType != previewLocation) {
            writer = when (previewLocation) {
                PreviewLocation.FILE_END -> EndFilePsiElementsWriter()
                PreviewLocation.AFTER_FUNCTION -> AfterTargetPsiElementsWriter()
            }
        }
    }


    fun write(
        project: Project,
        editor: Editor?,
        file: KtFile,
        functionWithPreview: FunctionWithPreview,
        newLine: PsiElement,
    ) {
        write(project, editor, file, listOf(functionWithPreview), newLine, false)
    }

    fun write(
        project: Project,
        editor: Editor?,
        file: KtFile,
        functionWithPreviews: List<FunctionWithPreview>,
        newLine: PsiElement,
        isCommandRequired: Boolean,
    ) {
        val writeLambda = {
            val shortenReferences = ShortenReferences.DEFAULT
            val codeStyleManager = CodeStyleManager.getInstance(project)

            val firstElementTextOffset: Int
            val addTime = measureTime {
                firstElementTextOffset = writer.addElementsToFile(file, functionWithPreviews, newLine)
            }
            println("Add time: $addTime")
            moveCaretAndScroll(editor, firstElementTextOffset)

            val commitTime = measureTime { file.commitAndUnblockDocument() }
            println("Commit time: $commitTime")

            val shortenTime = measureTime { shortenReferences.process(file) }
            println("Shorten time: $shortenTime")
            val reformatTime = measureTime {
                //                val changedRangesInfo = VcsFacade.getInstance().getChangedRangesInfo(file)
                //                if (changedRangesInfo != null) {
                //                    println("Reformat chages")
                //                    codeStyleManager.reformatChanges(file, changedRangesInfo,)
                //                } else {
                //                    println("Reformat")
                codeStyleManager.reformat(file)
                //                }
            } // ToDo file or psi?
            println("Reformat time: $reformatTime")
        }

        if (isCommandRequired) {
            project.executeWriteCommand(WRITE_COMPOSE_PREVIEW_COMMAND, writeLambda)
        } else {
            WriteAction.run<Throwable>(writeLambda)
        }
    }

    private fun moveCaretAndScroll(editor: Editor?, firstElementTextOffset: Int) {
        editor?.apply {
            println("Element position: $firstElementTextOffset")
            caretModel.primaryCaret.moveToOffset(firstElementTextOffset)
            scrollingModel.scrollToCaret(ScrollType.CENTER)
        }
    }

}
