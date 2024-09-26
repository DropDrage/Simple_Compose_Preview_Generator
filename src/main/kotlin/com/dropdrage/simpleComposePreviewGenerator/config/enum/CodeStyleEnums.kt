package com.dropdrage.simpleComposePreviewGenerator.config.enum

import com.dropdrage.simpleComposePreviewGenerator.config.Titled
import com.dropdrage.simpleComposePreviewGenerator.utils.i18n.SimpleComposePreviewGeneratorBundle.message

enum class FirstAnnotation(override val title: String) : Titled {
    PREVIEW(message("settings.codeStyle.firstAnnotation.preview")),
    COMPOSABLE(message("settings.codeStyle.firstAnnotation.composable")),
}

enum class PreviewBodyType(override val title: String) : Titled {
    EXPRESSION(message("settings.codeStyle.previewFunction.bodyType.expression")),
    BLOCK(message("settings.codeStyle.previewFunction.bodyType.block")),
}

enum class PreviewLocation(override val title: String) : Titled {
    FILE_END(message("settings.codeStyle.previewFunction.location.endOfFile")),
    AFTER_FUNCTION(message("settings.codeStyle.previewFunction.location.afterFunction")),
}
