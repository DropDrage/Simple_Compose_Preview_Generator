package com.dropdrage.simpleComposePreviewGenerator.config

import com.dropdrage.simpleComposePreviewGenerator.config.enum.FirstAnnotation
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewBodyType
import com.dropdrage.simpleComposePreviewGenerator.config.enum.PreviewLocation
import com.intellij.openapi.components.BaseState

class Config : BaseState() {

    //region Code Style
    var firstAnnotation by enum(FirstAnnotation.PREVIEW)
    var previewBodyType by enum(PreviewBodyType.BLOCK)
    var previewLocation by enum(PreviewLocation.FILE_END)
    var isTrailingCommaEnabled by property(true)
    //endregion

    //region Generation
    var isDefaultsGenerationEnabled by property(false)
    var isModifierGenerationEnabled by property(true)
    var fillNullableWithNulls by property(false)
    var isThemeEnabled by property(true)

    var isEmptyBuilderForListEnabled by property(false)
    var isEmptyBuilderForSetEnabled by property(false)
    var isEmptyBuilderForMapEnabled by property(false)
    var isEmptyBuilderForArrayEnabled by property(false)
    var isEmptyBuilderForSequenceEnabled by property(false)
    //endregion

}
