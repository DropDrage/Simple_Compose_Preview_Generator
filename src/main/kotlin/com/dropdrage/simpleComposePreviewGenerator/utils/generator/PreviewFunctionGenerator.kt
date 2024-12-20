/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.generator

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Classes

@Suppress("NOTHING_TO_INLINE")
internal object PreviewFunctionGenerator { // ToDo generate PSI instead of string

    private val isPreviewFirstSetting: Boolean
        get() = ConfigService.config.firstAnnotation == FirstAnnotation.PREVIEW
    private val isPreviewHasExpressionBody: Boolean
        get() = ConfigService.config.previewBodyType == PreviewBodyType.EXPRESSION
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled

    private val previewFunctionNameSuffix: String
        get() = ConfigService.config.notNullPreviewFunctionNameSuffix


    fun generateString(
        annotations: AnnotationsSet,
        theme: String?,
        functionName: String,
        argumentsList: ArgumentsList,
    ): String = buildString {
        append('\n')

        if (isPreviewFirstSetting) {
            appendAnnotation(annotations.preview)
            appendAnnotation(annotations.composable)
        } else {
            appendAnnotation(annotations.composable)
            appendAnnotation(annotations.preview)
        }
        append("private fun ").append(functionName).append(previewFunctionNameSuffix).append("() ")
        val isExpressionBody = theme != null && isPreviewHasExpressionBody
        if (isExpressionBody) {
            append("= ")
        } else {
            append("{\n")
        }

        if (theme != null) append(theme).append("{\n")
        else if (isThemeEnabled) append("/* TODO WARN: Accessible theme is not found */\n")

        append(functionName).append('(')
        if (argumentsList is ArgumentsList.HasArgumentsButInsertedOutside) {
            append('\n')

            if (argumentsList is ArgumentsList.HasArguments) {
                append(argumentsList.arguments)
            }
        }
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

    sealed interface ArgumentsList {
        object NoArguments : ArgumentsList
        open class HasArgumentsButInsertedOutside() : ArgumentsList
        class HasArguments(val arguments: String) : HasArgumentsButInsertedOutside() {
            init {
                require(arguments.isNotBlank())
            }
        }
    }

}
