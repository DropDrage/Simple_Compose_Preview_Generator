/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.config

import com.dropdrage.simpleComposePreviewGenerator.config.Config.Companion.DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX
import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewLocation
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewGenerationSettingsChangePublisher
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewPositionChangePublisher
import com.dropdrage.simpleComposePreviewGenerator.utils.i18n.SimpleComposePreviewGeneratorBundle.message
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.observable.properties.AtomicBooleanProperty
import com.intellij.openapi.observable.util.whenMousePressed
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.validation.CHECK_NON_EMPTY
import com.intellij.openapi.ui.validation.CHECK_NO_WHITESPACES
import com.intellij.openapi.ui.validation.validationErrorIf
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.not
import com.intellij.ui.layout.selected
import kotlin.reflect.KMutableProperty0

@Suppress("NOTHING_TO_INLINE")
internal class MainConfigurable : BoundSearchableConfigurable(
    message("settings.title"),
    "SimpleComposePreviewGenerator",
) {

    private val invalidCharactersValidationRegex = INVALID_CHARACTERS_VALIDATION_REGEX.toRegex()


    override fun createPanel() = panel {
        generalSettings()

        codeStyleSettings()
        generationSimplificationSettings()
    }

    private fun Panel.generalSettings() = group(title = message("settings.general.title")) {
        row {
            val isResetButtonVisible = AtomicBooleanProperty(
                ConfigService.config.previewFunctionNameSuffix != DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX,
            )
            val previewSuffixField = textField()
                .label(message("settings.general.preview.suffix.label"))
                .bindText(
                    ConfigService.config::previewFunctionNameSuffix.toNonNullableProperty(
                        DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX,
                    )
                )
                .textValidation(
                    CHECK_NO_WHITESPACES,
                    validationErrorIf<String>(
                        message("settings.general.preview.suffix.error.invalidCharacters"),
                        invalidCharactersValidationRegex::containsMatchIn,
                    ),
                )
                .trimmedTextValidation(CHECK_NON_EMPTY)
                .whenTextChangedFromUi { isResetButtonVisible.set(it != DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX) }

            cell(
                JBLabel(AllIcons.General.Reset).apply {
                    whenMousePressed {
                        previewSuffixField.text(DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX)
                        isResetButtonVisible.set(false)
                    }
                }
            ).visibleIf(isResetButtonVisible)
        }
    }

    private fun Panel.codeStyleSettings() = group(title = message("settings.codeStyle.title")) {
        labeledComboBoxRow<FirstAnnotation>(
            message("settings.codeStyle.firstAnnotation.label"),
            ConfigService.config::firstAnnotation.toNullableProperty(),
        )
        labeledComboBoxRow<PreviewBodyType>(
            message("settings.codeStyle.previewFunction.bodyType.label"),
            ConfigService.config::previewBodyType.toNullableProperty(),
        )
        labeledComboBoxRow<PreviewLocation>(
            message("settings.codeStyle.previewFunction.location.label"),
            ConfigService.config::previewLocation.toNullableProperty(),
        )

        checkBoxRow(
            message("settings.codeStyle.trailingComma.label"),
            ConfigService.config::isTrailingCommaEnabled,
        ).topGap(TopGap.SMALL)
        checkBoxRow(
            message("settings.codeStyle.forceBlankLineBeforePreview.label"),
            ConfigService.config::isSingleBlankLineBeforePreviewForced,
        ).topGap(TopGap.SMALL).contextHelp(message("settings.codeStyle.forceBlankLineBeforePreview.help"))
    }

    private fun Panel.generationSimplificationSettings() = group(message("settings.argumentsGeneration.title")) {
        lateinit var generateDefaultsCheckBox: Cell<JBCheckBox>
        row {
            generateDefaultsCheckBox = checkBox(
                message("settings.argumentsGeneration.generateFunctionParamsWithDefaultValues.label"),
                ConfigService.config::isDefaultsGenerationEnabled,
            )
        }
        indent {
            checkBoxRow(
                message("settings.argumentsGeneration.skipViewModel.label"),
                ConfigService.config::isSkipViewModel
            ).enabledIf(generateDefaultsCheckBox.selected)
            row {
                val modifierGenerationCheckBox = checkBox(
                    message("settings.argumentsGeneration.generateModifier.label"),
                    ConfigService.config::isModifierGenerationEnabled,
                ).enabledIf(generateDefaultsCheckBox.selected.not())

                generateDefaultsCheckBox.onChanged {
                    val isGenerateDefaultsSelected = it.selected.invoke()
                    modifierGenerationCheckBox.selected(isGenerateDefaultsSelected)
                }
            }
        }

        checkBoxRow(
            message("settings.argumentsGeneration.assignNullValueForNullableArguments"),
            ConfigService.config::isFillNullableWithNullsEnabled,
        ).topGap(TopGap.SMALL)
        checkBoxRow(message("settings.argumentsGeneration.addTheme.label"), ConfigService.config::isThemeEnabled)
            .contextHelp(message("settings.argumentsGeneration.addTheme.tooltip"))

        row { text(message("settings.argumentsGeneration.useEmptyCollectionBuilders.label")) }
            .topGap(TopGap.SMALL)
            .contextHelp(message("settings.argumentsGeneration.useEmptyCollectionBuilders.help"))
        indent {
            row {
                checkBox(
                    message("settings.argumentsGeneration.useEmptyCollectionBuilders.array"),
                    ConfigService.config::isEmptyBuilderForArrayEnabled,
                )
                checkBox(
                    message("settings.argumentsGeneration.useEmptyCollectionBuilders.iterableCollectionList"),
                    ConfigService.config::isEmptyBuilderForListEnabled,
                )
                checkBox(
                    message("settings.argumentsGeneration.useEmptyCollectionBuilders.sequence"),
                    ConfigService.config::isEmptyBuilderForSequenceEnabled,
                )
            }.layout(RowLayout.PARENT_GRID)
            row {
                checkBox(
                    message("settings.argumentsGeneration.useEmptyCollectionBuilders.set"),
                    ConfigService.config::isEmptyBuilderForSetEnabled,
                )
                checkBox(
                    message("settings.argumentsGeneration.useEmptyCollectionBuilders.map"),
                    ConfigService.config::isEmptyBuilderForMapEnabled,
                )
            }.layout(RowLayout.PARENT_GRID)
        }
    }

    private inline fun Panel.checkBoxRow(text: String, prop: KMutableProperty0<Boolean>) = row {
        checkBox(text, prop)
    }

    private inline fun Row.checkBox(text: String, prop: KMutableProperty0<Boolean>) = checkBox(text).bindSelected(prop)

    private inline fun <reified T> Panel.labeledComboBoxRow(
        title: String,
        prop: MutableProperty<T?>,
    ) where T : Enum<T>, T : Titled = row {
        labeledComboBox<T>(title).bindItem(prop = prop)
    }

    private inline fun <reified T> Row.labeledComboBox(title: String): Cell<ComboBox<T>> where T : Enum<T>, T : Titled =
        simpleComboBox<T>().label(title)

    private inline fun <reified T> Row.simpleComboBox() where T : Enum<T>, T : Titled = comboBox(
        model = EnumComboBoxModel(T::class.java),
        renderer = { _, value, _, _, _ -> JBLabel(value?.title.orEmpty()) },
    )


    override fun apply() {
        super.apply()

        invokeLater { PreviewGenerationSettingsChangePublisher.onChanged() }
        invokeLater { PreviewPositionChangePublisher.onPreviewPositionChanged() }
    }


    companion object {
        private const val INVALID_CHARACTERS_VALIDATION_REGEX = """\W+"""
    }

}
