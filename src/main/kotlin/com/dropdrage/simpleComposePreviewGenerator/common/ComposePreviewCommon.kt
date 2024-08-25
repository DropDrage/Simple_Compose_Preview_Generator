package com.dropdrage.simpleComposePreviewGenerator.common

import androidx.compose.ui.util.fastForEachIndexed
import com.dropdrage.simpleComposePreviewGenerator.common.DefaultValuesProvider.getDefaultForType
import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.index.ComposeThemeIndex
import com.dropdrage.simpleComposePreviewGenerator.utils.PreviewFunctionGenerator
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Constants.FUNCTION_ARGUMENTS_SEPARATOR
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.FqNames
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.descriptorWithVisibility
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.nj2k.types.typeFqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.calls.results.argumentValueType
import kotlin.time.measureTime

internal class ComposePreviewCommon { //ToDo to object?

    //region Settings
    private val shouldGenerateDefaults: Boolean
        get() = ConfigService.config.isDefaultsGenerationEnabled
    private val isModifierGenerationEnabled: Boolean
        get() = ConfigService.config.isModifierGenerationEnabled
    private val isThemeEnabled: Boolean
        get() = ConfigService.config.isThemeEnabled
    //endregion


    fun buildPreviewFunctionString(functionElement: KtNamedFunction, project: Project): String {
        val argumentsList: String
        val functionDeclarationDescriptor = functionElement.descriptorWithVisibility
        val argsTime = measureTime {
            argumentsList = buildCallParametersString(
                functionElement.valueParameters,
                functionDeclarationDescriptor,
            )
        }
        println("Args time: $argsTime")

        val theme =
            if (isThemeEnabled) ComposeThemeIndex.findAccessibleTheme(project, functionDeclarationDescriptor)
            else null

        return PreviewFunctionGenerator.generateString(functionElement.name!!, argumentsList, theme)
    }

    private fun buildCallParametersString(
        parameters: List<KtParameter>?,
        functionDeclarationDescriptor: DeclarationDescriptorWithVisibility,
    ): String = buildString {
        val shouldGenerateDefaults = shouldGenerateDefaults
        val isModifierGenerationEnabled = isModifierGenerationEnabled
        parameters?.fastForEachIndexed { i, parameter ->
            if (parameter.defaultValue == null
                || shouldGenerateDefaults
                || isModifierGenerationEnabled && parameter.typeFqName() == FqNames.Compose.MODIFIER
            ) {
                if (i != 0 && isNotEmpty()) { // avoids redundant "," before defaults
                    append(FUNCTION_ARGUMENTS_SEPARATOR).append('\n')
                }

                val descriptor = parameter.descriptor
                append(parameter.name)
                    .append(" = ")
                if (descriptor != null) {
                    append(getDefaultForType(descriptor.argumentValueType, functionDeclarationDescriptor))
                } else {
                    println("Descriptor null: ${parameter.name}")
                }
            }
        }
    }

}
