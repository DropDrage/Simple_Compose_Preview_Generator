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
import com.intellij.openapi.components.BaseState

class Config : BaseState() {

    //region General
    var previewFunctionNameSuffix by string(DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX)
    val notNullPreviewFunctionNameSuffix: String
        get() = previewFunctionNameSuffix ?: DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX
    //endregion

    //region Code Style
    var firstAnnotation by enum(FirstAnnotation.PREVIEW)
    var previewBodyType by enum(PreviewBodyType.BLOCK)
    var previewLocation by enum(PreviewLocation.FILE_END)
    var isTrailingCommaEnabled by property(true)
    var isSingleBlankLineBeforePreviewForced by property(true)
    //endregion

    //region Arguments Generation
    var isDefaultsGenerationEnabled by property(false)
    var isSkipViewModel by property(true)
    var isModifierGenerationEnabled by property(true)
    var isFillNullableWithNullsEnabled by property(false)
    var isThemeEnabled by property(true)

    var isEmptyBuilderForListEnabled by property(false)
    var isEmptyBuilderForSetEnabled by property(false)
    var isEmptyBuilderForMapEnabled by property(false)
    var isEmptyBuilderForArrayEnabled by property(false)
    var isEmptyBuilderForSequenceEnabled by property(false)
    //endregion


    companion object {
        internal const val DEFAULT_PREVIEW_FUNCTION_NAME_SUFFIX = "Preview"
    }

}
