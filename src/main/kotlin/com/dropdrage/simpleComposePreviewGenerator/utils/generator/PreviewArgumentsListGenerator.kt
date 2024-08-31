package com.dropdrage.simpleComposePreviewGenerator.utils.generator

import androidx.compose.ui.util.fastForEach
import com.dropdrage.simpleComposePreviewGenerator.common.DefaultValuesProvider.getDefaultForType
import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Constants.FUNCTION_ARGUMENTS_SEPARATOR
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.FqNames
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
import org.jetbrains.kotlin.nj2k.types.typeFqName
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.parameterIndex
import org.jetbrains.kotlin.resolve.calls.results.argumentValueType

internal class PreviewArgumentsListGenerator {

    private val shouldGenerateDefaults: Boolean
        get() = ConfigService.config.isDefaultsGenerationEnabled
    private val isModifierGenerationEnabled: Boolean
        get() = ConfigService.config.isModifierGenerationEnabled
    private val isTrailingCommaEnabled: Boolean
        get() = ConfigService.config.isTrailingCommaEnabled


    fun buildCallParametersString(parameters: List<KtParameter>?): String = buildString {
        val shouldGenerateDefaults = shouldGenerateDefaults
        val isModifierGenerationEnabled = isModifierGenerationEnabled
        parameters?.fastForEach { parameter ->
            if (parameter.defaultValue == null
                || shouldGenerateDefaults
                || isModifierGenerationEnabled && parameter.typeFqName() == FqNames.Compose.MODIFIER
            ) {
                if (isNotEmpty()) { // avoids redundant "," before defaults
                    append(CALL_ARGUMENTS_SEPARATOR)
                }

                append(parameter.name)
                    .append(" = ")
                val argumentValueType = parameter.descriptor?.argumentValueType
                if (argumentValueType != null) {
                    append(getDefaultForType(argumentValueType))
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
            val isModifierGenerationEnabled = isModifierGenerationEnabled
            var hasAnyParameterAdded = false
            val indent = getIndent(project)
            val autocompleteExpression = MacroCallNode(CompleteSmartMacro())
            parameters?.fastForEach { parameter ->
                if (parameter.defaultValue == null
                    || shouldGenerateDefaults
                    || isModifierGenerationEnabled && parameter.typeFqName() == FqNames.Compose.MODIFIER
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

    companion object {
        private const val CALL_ARGUMENTS_SEPARATOR = "$FUNCTION_ARGUMENTS_SEPARATOR\n"

        private val LOG = logger<PreviewArgumentsListGenerator>()
    }

}
