package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import org.jetbrains.kotlin.psi.KtAnnotationEntry

internal inline val KtAnnotationEntry.shortNameStringSafe: String?
    get() = shortName?.asString()
