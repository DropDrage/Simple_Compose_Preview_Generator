/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.config

import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewLocation
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewGenerationSettingsChangePublisher
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewPositionChangePublisher
import com.dropdrage.simpleComposePreviewGenerator.utils.i18n.SimpleComposePreviewGeneratorBundle.message
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.EnumComboBoxModel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.not
import com.intellij.ui.layout.selected
import javax.swing.JLabel
import kotlin.reflect.KMutableProperty0

@Suppress("NOTHING_TO_INLINE")
class MainConfigurable : BoundSearchableConfigurable(message("settings.title"), "SimpleComposePreviewGenerator") {

    override fun createPanel() = panel {
        codeStyleSettings()
        generationSimplificationSettings()
    }

    private fun Panel.codeStyleSettings() = group(title = message("settings.codeStyle.title")) {
        labeledComboBoxRow<FirstAnnotation>(
            message("settings.codeStyle.firstAnnotation.label"),
            ConfigService.config::firstAnnotation.toNullableProperty(),
        )
        labeledComboBoxRow<PreviewBodyType>(
            message("settings.codeStyle.previewFunction.bodyType"),
            ConfigService.config::previewBodyType.toNullableProperty(),
        )
        labeledComboBoxRow<PreviewLocation>(
            message("settings.codeStyle.previewFunction.location"),
            ConfigService.config::previewLocation.toNullableProperty(),
        )

        checkBoxRow(message("settings.codeStyle.trailingComma.label"), ConfigService.config::isTrailingCommaEnabled)
            .topGap(TopGap.SMALL)
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
            )
                .enabledIf(generateDefaultsCheckBox.selected)
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
            .contextHelp(message("settings.argumentsGeneration.useEmptyCollectionBuilders.tooltip"))
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

//        row {
//            checkBox("Use null for nullable classes")
//                .bindSelected(prop = DefaultValuesProvider::useNull)
//                .align(AlignY.TOP)
//        }
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


    private inline fun <T : Titled> Row.labeledComboBox(title: String, list: List<T>): Cell<ComboBox<T>> {
        text(title)
        return simpleComboBox<T>(list)
    }

    private inline fun <T : Titled> Row.simpleComboBox(list: List<T>) = comboBox(
        model = CollectionComboBoxModel(list),
        renderer = { _, value, _, _, _ -> JLabel(value?.title) },
    )


    override fun apply() {
        super.apply()

        invokeLater {
            PreviewGenerationSettingsChangePublisher.onChanged()
        }
        invokeLater {
            PreviewPositionChangePublisher.onPreviewPositionChanged()
        }
    }

}
