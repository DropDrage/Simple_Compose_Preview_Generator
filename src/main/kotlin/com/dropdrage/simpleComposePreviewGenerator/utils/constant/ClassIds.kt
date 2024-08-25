package com.dropdrage.simpleComposePreviewGenerator.utils.constant

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

internal object ClassIds {
    object Annotation {
        val COMPOSE_PREVIEW = ClassId(Packages.COMPOSE_PREVIEW, Name.identifier(ShortNames.Annotation.PREVIEW))
    }
}