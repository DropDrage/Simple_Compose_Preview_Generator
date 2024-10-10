package com.dropdrage.simpleComposePreviewGenerator.common

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.index.ComposeThemeIndex
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.logTimeOnDebugResulted
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.descriptorWithVisibility
import com.dropdrage.simpleComposePreviewGenerator.utils.generator.PreviewArgumentsListGenerator
import com.dropdrage.simpleComposePreviewGenerator.utils.generator.PreviewFunctionGenerator
import com.intellij.codeInsight.template.Template
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.psi.KtNamedFunction

internal object GenerateComposePreviewCommon {

    private val LOG = thisLogger()

    //region Settings
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled
    //endregion

    private val previewArgumentsListGenerator = PreviewArgumentsListGenerator()

    fun buildPreviewStringOfPreviewFunction(functionElement: KtNamedFunction, project: Project): String {
        val theme = getTheme(functionElement, project, ComposeThemeIndex::findAccessibleThemeName)
        val argumentsList = buildPreviewFunctionArgumentsString(
            functionElement,
            DefaultValuesProvider.DefaultsSet.ONLY_NAMES,
        )

        return PreviewFunctionGenerator.generateString(
            PreviewFunctionGenerator.AnnotationsSet.ONLY_NAMES,
            theme,
            functionElement.name!!,
            argumentsList,
        )
    }

    fun buildPreviewFunctionString(functionElement: KtNamedFunction, project: Project): String {
        val argumentsList = buildPreviewFunctionArgumentsString(functionElement)
        return buildPreviewFunctionString(functionElement, project, argumentsList)
    }

    private fun buildPreviewFunctionArgumentsString(
        functionElement: KtNamedFunction,
        defaultsSet: DefaultValuesProvider.DefaultsSet = DefaultValuesProvider.DefaultsSet.WITH_FQ,
    ): String = LOG.logTimeOnDebugResulted("Args string") {
        previewArgumentsListGenerator.buildCallParametersString(functionElement.valueParameters, defaultsSet)
    }

    fun buildPreviewFunctionStringForTemplate(functionElement: KtNamedFunction, project: Project): String =
        buildPreviewFunctionString(functionElement, project, "")

    private fun buildPreviewFunctionString(
        functionElement: KtNamedFunction,
        project: Project,
        argumentsList: String,
    ): String = LOG.logTimeOnDebugResulted("Build Preview String") {
        val theme = getTheme(functionElement, project, ComposeThemeIndex::findAccessibleThemeFq)

        PreviewFunctionGenerator.generateString(
            PreviewFunctionGenerator.AnnotationsSet.WITH_FQ,
            theme,
            functionElement.name!!,
            argumentsList,
        )
    }

    private fun getTheme(
        functionElement: KtNamedFunction,
        project: Project,
        findAccessibleTheme: (Project, DeclarationDescriptorWithVisibility) -> String?,
    ): String? = if (isThemeEnabled) {
        val functionDeclarationDescriptor = functionElement.descriptorWithVisibility
        findAccessibleTheme(project, functionDeclarationDescriptor)
    } else null


    fun buildPreviewFunctionArgumentsTemplate(
        functionElement: KtNamedFunction,
        project: Project,
    ): Template = LOG.logTimeOnDebugResulted("Args template") {
        previewArgumentsListGenerator.buildCallParametersTemplate(functionElement.valueParameters, project)
    }

}
