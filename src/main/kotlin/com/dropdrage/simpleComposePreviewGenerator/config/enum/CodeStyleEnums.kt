package com.dropdrage.simpleComposePreviewGenerator.config.enum

import com.dropdrage.simpleComposePreviewGenerator.config.Titled

enum class FirstAnnotation(override val title: String) : Titled {
    PREVIEW("@Preview"),
    COMPOSABLE("@Composable"),
}

enum class PreviewBodyType(override val title: String) : Titled {
    EXPRESSION("Expression ="),
    BLOCK("Block {}"),
}

enum class PreviewLocation(override val title: String) : Titled {
    FILE_END("End of file"),
    AFTER_FUNCTION("After function"),
}
