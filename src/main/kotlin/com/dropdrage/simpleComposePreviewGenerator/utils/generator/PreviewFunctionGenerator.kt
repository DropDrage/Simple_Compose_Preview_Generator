package com.dropdrage.simpleComposePreviewGenerator.utils.generator

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
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled


    fun generateString(functionName: String, argumentsList: String, theme: String?): String = buildString {
        append('\n')

        if (isPreviewFirstSetting) {
            appendAnnotation(FqNameStrings.Compose.Annotation.PREVIEW)
            appendAnnotation(FqNameStrings.Compose.Annotation.COMPOSABLE)
        } else {
            appendAnnotation(FqNameStrings.Compose.Annotation.COMPOSABLE)
            appendAnnotation(FqNameStrings.Compose.Annotation.PREVIEW)
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

}
