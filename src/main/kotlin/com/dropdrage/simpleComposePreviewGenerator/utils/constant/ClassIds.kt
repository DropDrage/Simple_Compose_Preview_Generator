package com.dropdrage.simpleComposePreviewGenerator.utils.constant

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

internal object ClassIds {
    object Annotation {
        object Compose {
            val PREVIEW = ClassId(Packages.Compose.PREVIEW, Name.identifier(ShortNames.Compose.Annotation.PREVIEW))
        }
    }
}