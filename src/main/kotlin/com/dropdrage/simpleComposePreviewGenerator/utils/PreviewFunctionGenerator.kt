package com.dropdrage.simpleComposePreviewGenerator.utils

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.FqNameStrings

@Suppress("NOTHING_TO_INLINE")
internal object PreviewFunctionGenerator {

    private val isPreviewFirstSetting: Boolean
        get() = ConfigService.config.firstAnnotation == FirstAnnotation.PREVIEW
    private val isPreviewHasExpressionBody: Boolean
        get() = ConfigService.config.previewBodyType == PreviewBodyType.EXPRESSION
    private val isTrailingCommaEnabled: Boolean
        get() = ConfigService.config.isTrailingCommaEnabled
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled


    fun generateString(functionName: String, argumentsList: String, theme: String?): String = buildString {
        append('\n')

        if (isPreviewFirstSetting) {
            appendAnnotation(FqNameStrings.Annotation.COMPOSE_PREVIEW_ANNOTATION)
            appendAnnotation(FqNameStrings.Annotation.COMPOSE_ANNOTATION)
        } else {
            appendAnnotation(FqNameStrings.Annotation.COMPOSE_ANNOTATION)
            appendAnnotation(FqNameStrings.Annotation.COMPOSE_PREVIEW_ANNOTATION)
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
        if (isTrailingCommaEnabled) append(",\n")
        append(")\n")

        if (theme != null) append("}\n")

        if (!isExpressionBody) append("}\n")
    }

    private inline fun StringBuilder.appendAnnotation(annotation: String) {
        append('@').append(annotation).append('\n')
    }

}
