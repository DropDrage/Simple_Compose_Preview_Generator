@file:Suppress("NOTHING_TO_INLINE")

package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import com.android.tools.compose.isComposableAnnotation
import com.android.tools.idea.kotlin.fqNameMatches
import com.android.tools.idea.kotlin.hasAnnotation
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.ClassIds
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.FqNameStrings
import org.jetbrains.kotlin.psi.KtNamedFunction

internal fun KtNamedFunction.isTargetForComposePreview(): Boolean {
    var isComposableFunction = false
    for (annotation in annotationEntries) {
        if (!isComposableFunction && annotation.isComposableAnnotation()) {
            isComposableFunction = true
        } else if (annotation.fqNameMatches(FqNameStrings.Annotation.COMPOSE_PREVIEW_ANNOTATION)) {
            println("//// return false")
            return false
        }
    }

    println("//// $isComposableFunction")
    return isComposableFunction
}

internal inline fun KtNamedFunction.isComposePreviewFunction(): Boolean =
    hasAnnotation(ClassIds.Annotation.COMPOSE_PREVIEW)

internal inline val KtNamedFunction.fqNameString: String
    get() = fqName!!.asString()
