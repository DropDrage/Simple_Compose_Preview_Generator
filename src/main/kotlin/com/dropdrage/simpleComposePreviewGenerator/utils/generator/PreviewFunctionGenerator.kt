package com.dropdrage.simpleComposePreviewGenerator.utils.generator

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Classes

@Suppress("NOTHING_TO_INLINE")
internal object PreviewFunctionGenerator {

    private val isPreviewFirstSetting: Boolean
        get() = ConfigService.config.firstAnnotation == FirstAnnotation.PREVIEW
    private val isPreviewHasExpressionBody: Boolean
        get() = ConfigService.config.previewBodyType == PreviewBodyType.EXPRESSION
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled


    fun generateString(
        annotations: AnnotationsSet,
        theme: String?,
        functionName: String,
        argumentsList: String,
    ): String = buildString {
        append('\n')

        if (isPreviewFirstSetting) {
            appendAnnotation(annotations.preview)
            appendAnnotation(annotations.composable)
        } else {
            appendAnnotation(annotations.composable)
            appendAnnotation(annotations.preview)
        }
        append("private fun ").append(functionName).append("Preview() ")
        val isExpressionBody = theme != null && isPreviewHasExpressionBody
        if (isExpressionBody) {
            append("= ")
        } else {
            append("{\n")
        }

        if (theme != null) append(theme).append("{\n")
        else if (isThemeEnabled) append("/* TODO WARN: Accessible theme is not found */\n")

        append(functionName).append("(\n")
            .append(argumentsList)
        append(")\n")

        if (theme != null) append("}\n")

        if (!isExpressionBody) append("}\n")
    }

    private inline fun StringBuilder.appendAnnotation(annotation: String) {
        append('@').append(annotation).append('\n')
    }


    enum class AnnotationsSet(
        val preview: String,
        val composable: String,
    ) {
        WITH_FQ(
            Classes.Compose.Annotation.Preview.FQ_STRING,
            Classes.Compose.Annotation.Composable.FQ_STRING,
        ),
        ONLY_NAMES(
            Classes.Compose.Annotation.Preview.SHORT_NAME,
            Classes.Compose.Annotation.Composable.SHORT_NAME,
        ),
    }

}
