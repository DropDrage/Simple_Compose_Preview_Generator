package com.dropdrage.simpleComposePreviewGenerator.common

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.index.ComposeThemeIndex
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebug
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.descriptorWithVisibility
import com.dropdrage.simpleComposePreviewGenerator.utils.generator.PreviewArgumentsListGenerator
import com.dropdrage.simpleComposePreviewGenerator.utils.generator.PreviewFunctionGenerator
import com.intellij.codeInsight.template.Template
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtNamedFunction

internal class GenerateComposePreviewCommon { //ToDo to object?

    //region Settings
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled
    //endregion

    private val previewArgumentsListGenerator = PreviewArgumentsListGenerator()


    fun buildPreviewFunctionString(functionElement: KtNamedFunction, project: Project): String {
        val argumentsList: String = buildPreviewFunctionArgumentsString(functionElement)
        return buildPreviewFunctionString(functionElement, project, argumentsList)
    }

    private fun buildPreviewFunctionArgumentsString(functionElement: KtNamedFunction): String {
        val argumentsList: String
        LOG.logTimeOnDebug("Args string") {
            argumentsList = previewArgumentsListGenerator.buildCallParametersString(functionElement.valueParameters)
        }
        return argumentsList
    }

    fun buildPreviewFunctionStringForTemplate(functionElement: KtNamedFunction, project: Project): String =
        buildPreviewFunctionString(functionElement, project, "")

    private fun buildPreviewFunctionString(
        functionElement: KtNamedFunction,
        project: Project,
        argumentsList: String,
    ): String {
        val theme =
            if (isThemeEnabled) {
                val functionDeclarationDescriptor = functionElement.descriptorWithVisibility
                ComposeThemeIndex.findAccessibleTheme(project, functionDeclarationDescriptor)
            } else null

        return PreviewFunctionGenerator.generateString(functionElement.name!!, argumentsList, theme)
    }


    fun buildPreviewFunctionArgumentsTemplate(
        functionElement: KtNamedFunction,
        project: Project,
    ): Template {
        val argumentsList: Template
        LOG.logTimeOnDebug("Args template") {
            argumentsList = previewArgumentsListGenerator.buildCallParametersTemplate(
                functionElement.valueParameters,
                project,
            )
        }

        return argumentsList
    }


    companion object {
        private val LOG = logger<GenerateComposePreviewCommon>()
    }

}
