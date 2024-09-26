package com.dropdrage.simpleComposePreviewGenerator.utils.generator

import androidx.compose.ui.util.fastForEach
import com.dropdrage.simpleComposePreviewGenerator.common.DefaultValuesProvider
import com.dropdrage.simpleComposePreviewGenerator.common.DefaultValuesProvider.getDefaultForType
import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Classes
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Constants.FUNCTION_ARGUMENTS_SEPARATOR
import com.intellij.application.options.CodeStyle
import com.intellij.codeInsight.template.Template
import com.intellij.codeInsight.template.TemplateManager
import com.intellij.codeInsight.template.impl.ConstantNode
import com.intellij.codeInsight.template.impl.MacroCallNode
import com.intellij.codeInsight.template.macro.CompleteSmartMacro
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.nj2k.types.typeFqName
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.parameterIndex
import org.jetbrains.kotlin.resolve.calls.results.argumentValueType

internal class PreviewArgumentsListGenerator {

    private val shouldGenerateDefaults: Boolean
        get() = ConfigService.config.isDefaultsGenerationEnabled
    private val isSkipViewModel: Boolean
        get() = ConfigService.config.isSkipViewModel
    private val isModifierGenerationEnabled: Boolean
        get() = ConfigService.config.isModifierGenerationEnabled
    private val isTrailingCommaEnabled: Boolean
        get() = ConfigService.config.isTrailingCommaEnabled


    fun buildCallParametersString(
        parameters: List<KtParameter>?,
        defaultsSet: DefaultValuesProvider.DefaultsSet,
    ): String = buildString {
        val shouldGenerateDefaults = shouldGenerateDefaults
        val isSkipViewModel = isSkipViewModel
        val isModifierGenerationEnabled = isModifierGenerationEnabled
        parameters?.fastForEach { parameter ->
            if (
                shouldBeParameterAdded(
                    parameter,
                    shouldGenerateDefaults,
                    isSkipViewModel,
                    isModifierGenerationEnabled,
                )
            ) {
                if (isNotEmpty()) { // avoids redundant "," before defaults
                    append(CALL_ARGUMENTS_SEPARATOR)
                }

                append(parameter.name)
                    .append(" = ")
                val argumentValueType = parameter.descriptor?.argumentValueType
                if (argumentValueType != null) {
                    append(getDefaultForType(argumentValueType, defaultsSet))
                } else {
                    LOG.warn("Descriptor null: ${parameter.name}")
                }
            }
        }

        if (isTrailingCommaEnabled) {
            append(FUNCTION_ARGUMENTS_SEPARATOR)
        }
    }

    fun buildCallParametersTemplate(parameters: List<KtParameter>?, project: Project): Template {
        val templateManager = TemplateManager.getInstance(project)
        return templateManager.createTemplate("", "").apply {
            addTextSegment("\n")

            val shouldGenerateDefaults = shouldGenerateDefaults
            val isSkipViewModel = isSkipViewModel
            val isModifierGenerationEnabled = isModifierGenerationEnabled
            var hasAnyParameterAdded = false
            val indent = getIndent(project)
            val autocompleteExpression = MacroCallNode(CompleteSmartMacro())
            parameters?.fastForEach { parameter ->
                if (
                    shouldBeParameterAdded(
                        parameter,
                        shouldGenerateDefaults,
                        isSkipViewModel,
                        isModifierGenerationEnabled,
                    )
                ) {
                    if (hasAnyParameterAdded) { // avoids redundant "," before defaults
                        addTextSegment(CALL_ARGUMENTS_SEPARATOR)
                    }
                    hasAnyParameterAdded = true

                    addTextSegment(indent)
                    val name = parameter.name ?: "unresolved_parameter_name${parameter.parameterIndex()}"
                    addTextSegment(name)
                    addTextSegment(" = ")
                    val argumentValueType = parameter.descriptor?.argumentValueType
                    addVariable(
                        name,
                        autocompleteExpression,
                        if (argumentValueType != null) ConstantNode(getDefaultForType(argumentValueType)) else null,
                        true,
                    )
                }
            }

            if (isTrailingCommaEnabled) {
                addTextSegment("$FUNCTION_ARGUMENTS_SEPARATOR")
            }
        }
    }

    private fun getIndent(project: Project): String {
        val indentOptions = CodeStyle.getSettings(project).getIndentOptions(KotlinFileType.INSTANCE)
        return if (indentOptions.USE_TAB_CHARACTER) "\t"
        else " ".repeat(indentOptions.INDENT_SIZE)
    }

    private fun shouldBeParameterAdded(
        parameter: KtParameter,
        shouldGenerateDefaults: Boolean,
        isSkipViewModel: Boolean,
        isModifierGenerationEnabled: Boolean,
    ): Boolean =
        parameter.defaultValue == null || run {
            val typeFqName = parameter.typeFqName()
            shouldGenerateDefaults && !shouldSkipViewModel(isSkipViewModel, typeFqName)
                || isModifierGenerationEnabled && typeFqName == Classes.Compose.Modifier.FQ_NAME
        }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun shouldSkipViewModel(isSkipViewModel: Boolean, typeFqName: FqName?): Boolean =
        isSkipViewModel && typeFqName == Classes.Android.ViewModel.FQ_NAME


    companion object {
        private const val CALL_ARGUMENTS_SEPARATOR = "$FUNCTION_ARGUMENTS_SEPARATOR\n"

        private val LOG = logger<PreviewArgumentsListGenerator>()
    }

}
