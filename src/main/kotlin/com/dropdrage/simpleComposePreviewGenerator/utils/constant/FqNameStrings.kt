package com.dropdrage.simpleComposePreviewGenerator.utils.constant

internal object FqNameStrings {

    object Annotation {
        const val COMPOSE_PREVIEW_ANNOTATION = "androidx.compose.ui.tooling.preview.${ShortNames.Annotation.PREVIEW}"
        const val COMPOSE_ANNOTATION = "androidx.compose.runtime.${ShortNames.Annotation.COMPOSABLE}"
    }

    const val COMPOSE_MATERIAL_THEME = "androidx.compose.material.${ShortNames.Function.MATERIAL_THEME}"
    const val COMPOSE_MATERIAL3_THEME = "androidx.compose.material3.${ShortNames.Function.MATERIAL_THEME}"

    const val COMPOSE_MODIFIER = "androidx.compose.ui.Modifier"

    const val CHAR_SEQUENCE = "kotlin.CharSequence"

}
