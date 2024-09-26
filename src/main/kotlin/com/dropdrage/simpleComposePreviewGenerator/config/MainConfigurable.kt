package com.dropdrage.simpleComposePreviewGenerator.config

import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewLocation
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewGenerationSettingsChangePublisher
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewPositionChangePublisher
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.options.BoundConfigurable
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
class MainConfigurable : BoundConfigurable("Simple Compose Preview Generator") {

    override fun createPanel() = panel {
        codeStyleSettings()
        generationSimplificationSettings()
    }

    private fun Panel.codeStyleSettings() = group(title = "Code style") {
        labeledComboBoxRow<FirstAnnotation>(
            "First annotation: ",
            ConfigService.config::firstAnnotation.toNullableProperty(),
        )
        labeledComboBoxRow<PreviewBodyType>(
            "Preview function body type: ",
            ConfigService.config::previewBodyType.toNullableProperty(),
        )
        labeledComboBoxRow<PreviewLocation>(
            "Preview function location: ",
            ConfigService.config::previewLocation.toNullableProperty(),
        )

        checkBoxRow("Trailing comma", ConfigService.config::isTrailingCommaEnabled)
            .topGap(TopGap.SMALL)
    }

    private fun Panel.generationSimplificationSettings() = group("Arguments generation") {
        lateinit var generateDefaultsCheckBox: Cell<JBCheckBox>
        row {
            generateDefaultsCheckBox = checkBox(
                "Generate function params with default values",
                ConfigService.config::isDefaultsGenerationEnabled,
            )
        }
        indent {
            checkBoxRow("Skip ViewModel", ConfigService.config::isSkipViewModel)
                .enabledIf(generateDefaultsCheckBox.selected)
            row {
                val modifierGenerationCheckBox = checkBox(
                    "Generate Modifier",
                    ConfigService.config::isModifierGenerationEnabled,
                ).enabledIf(generateDefaultsCheckBox.selected.not())

                generateDefaultsCheckBox.onChanged {
                    val isGenerateDefaultsSelected = it.selected.invoke()
                    modifierGenerationCheckBox.selected(isGenerateDefaultsSelected)
                }
            }
        }

        checkBoxRow("Assign null value for nullable arguments", ConfigService.config::isFillNullableWithNullsEnabled)
            .topGap(TopGap.SMALL)
        checkBoxRow("Add theme", ConfigService.config::isThemeEnabled)
            .contextHelp(
                "Theme file must end with \"Theme\" suffix.\n" +
                    "Theme function must have @Composable without @Preview annotation, " +
                    "name with \"Theme\" suffix and " +
                    "MaterialTheme call",
            )

        row { text("Use empty collection builders for") }
            .topGap(TopGap.SMALL)
            .contextHelp("Such as emptyArray(), emptyList(), emptySet(), emptyMap() and etc.")
        indent {
            row {
                checkBox("Array", ConfigService.config::isEmptyBuilderForArrayEnabled)
                checkBox("Iterable, Collection, List", ConfigService.config::isEmptyBuilderForListEnabled)
                checkBox("Sequence", ConfigService.config::isEmptyBuilderForSequenceEnabled)
            }.layout(RowLayout.PARENT_GRID)
            row {
                checkBox("Set", ConfigService.config::isEmptyBuilderForSetEnabled)
                checkBox("Map", ConfigService.config::isEmptyBuilderForMapEnabled)
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
