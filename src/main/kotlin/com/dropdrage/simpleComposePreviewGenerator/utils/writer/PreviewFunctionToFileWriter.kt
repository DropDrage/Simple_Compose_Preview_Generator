/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import ai.grazie.utils.applyIf
import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebugResulted
import com.intellij.application.options.CodeStyle
import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.codeinsight.utils.commitAndUnblockDocument
import org.jetbrains.kotlin.idea.core.ShortenReferences
import org.jetbrains.kotlin.idea.core.formatter.KotlinCodeStyleSettings
import org.jetbrains.kotlin.idea.util.application.executeWriteCommand
import org.jetbrains.kotlin.psi.KtFile

internal object PreviewFunctionToFileWriter {

    private const val WRITE_COMPOSE_PREVIEW_COMMAND = "write_compose_preview"

    private val LOG = thisLogger()

    private val writer: BasePreviewWriter
        get() = PreviewWriterProvider.writer

    private val isSingleBlankLineBeforePreviewForced: Boolean
        get() = ConfigService.config.isSingleBlankLineBeforePreviewForced


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
        val writeAction = PreviewWriteAction.Builder()
            .writePreview(editor, file, functionWithPreviews, newLine, shouldMoveToArgumentsListStart, afterWriteAction)
            .applyIf(isSingleBlankLineBeforePreviewForced) { forceBlankLineBeforePreview(project) }
            .build()

        if (isCommandRequired) {
            project.executeWriteCommand(WRITE_COMPOSE_PREVIEW_COMMAND, writeAction)
        } else {
            WriteAction.run<Throwable>(writeAction)
        }
    }

    private fun moveCaretAndScroll(editor: Editor?, firstElementTextOffset: Int) {
        editor?.apply {
            LOG.debug("Element position: $firstElementTextOffset")
            caretModel.primaryCaret.moveToOffset(firstElementTextOffset)
            scrollingModel.scrollToCaret(ScrollType.CENTER)
        }
    }


    private abstract class PreviewWriteAction : () -> Unit {

        private class PreviewWrite(
            private val editor: Editor?,
            private val file: KtFile,
            private val functionWithPreviews: List<FunctionWithPreview>,
            private val newLine: PsiElement,
            private val shouldMoveToArgumentsListStart: Boolean,
            private val afterWriteAction: (() -> Unit)? = null,
        ) : PreviewWriteAction() {
            override fun invoke() {
                val shortenReferences = ShortenReferences.DEFAULT

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

                afterWriteAction?.invoke()
            }
        }

        private class ForceBlankLineBeforePreviewAction(
            private val project: Project,
            private val action: PreviewWriteAction,
        ) : PreviewWriteAction() {

            override fun invoke() {
                val kotlinCodeStyleSettings =
                    CodeStyle.getSettings(project).getCustomSettings(KotlinCodeStyleSettings::class.java)
                val blankLinesBeforeDeclarationWithCommentOrAnnotation =
                    kotlinCodeStyleSettings.BLANK_LINES_BEFORE_DECLARATION_WITH_COMMENT_OR_ANNOTATION_ON_SEPARATE_LINE
                kotlinCodeStyleSettings.BLANK_LINES_BEFORE_DECLARATION_WITH_COMMENT_OR_ANNOTATION_ON_SEPARATE_LINE =
                    FORCED_BLANK_LINES_COUNT

                try {
                    action()
                } finally {
                    kotlinCodeStyleSettings.BLANK_LINES_BEFORE_DECLARATION_WITH_COMMENT_OR_ANNOTATION_ON_SEPARATE_LINE =
                        blankLinesBeforeDeclarationWithCommentOrAnnotation
                }
            }


            companion object {
                private const val FORCED_BLANK_LINES_COUNT = 1
            }

        }


        class Builder {

            private var previewWriteAction: PreviewWriteAction? = null


            fun writePreview(
                editor: Editor?,
                file: KtFile,
                functionWithPreviews: List<FunctionWithPreview>,
                newLine: PsiElement,
                shouldMoveToArgumentsListStart: Boolean,
                afterWriteAction: (() -> Unit)? = null,
            ) = apply {
                previewWriteAction = PreviewWrite(
                    editor,
                    file,
                    functionWithPreviews,
                    newLine,
                    shouldMoveToArgumentsListStart,
                    afterWriteAction,
                )
            }

            fun forceBlankLineBeforePreview(project: Project) = apply {
                previewWriteAction = ForceBlankLineBeforePreviewAction(
                    project,
                    previewWriteAction ?: error("Call smth else before use this"),
                )
            }

            fun build(): PreviewWriteAction = previewWriteAction ?: error("Nothing to build")

        }

    }

}
