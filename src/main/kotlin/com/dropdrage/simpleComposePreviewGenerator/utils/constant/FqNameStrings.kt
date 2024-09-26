package com.dropdrage.simpleComposePreviewGenerator.utils.constant

internal object FqNameStrings {

    object Compose {
        object Annotation {
            const val PREVIEW = "androidx.compose.ui.tooling.preview.${ShortNames.Compose.Annotation.PREVIEW}"
            const val COMPOSABLE = "androidx.compose.runtime.${ShortNames.Compose.Annotation.COMPOSABLE}"
        }

        const val MATERIAL_THEME = "androidx.compose.material.${ShortNames.Compose.Function.MATERIAL_THEME}"
        const val MATERIAL3_THEME = "androidx.compose.material3.${ShortNames.Compose.Function.MATERIAL_THEME}"

        const val MODIFIER = "androidx.compose.ui.${ShortNames.Compose.MODIFIER}"
    }

    const val ANDROID_VIEWMODEL = "androidx.lifecycle.ViewModel"

    const val CHAR_SEQUENCE = "kotlin.CharSequence"

}
