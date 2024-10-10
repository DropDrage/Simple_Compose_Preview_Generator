/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Classes
import com.intellij.openapi.diagnostic.thisLogger
import org.jetbrains.kotlin.idea.base.psi.KotlinPsiHeuristics
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction

internal fun KtNamedFunction.isTargetForComposePreview(): Boolean {
    val isComposableFunction = hasAnnotation(Classes.Compose.Annotation.Composable.FQ_NAME) &&
        !hasAnnotation(Classes.Compose.Annotation.Preview.FQ_NAME)
    KtNamedFunctionExtensions.LOG.debug("IsComposableFunction: $isComposableFunction")
    return isComposableFunction
}

internal inline fun KtNamedFunction.isComposePreviewFunction(): Boolean =
    hasAnnotation(Classes.Compose.Annotation.Preview.FQ_NAME)

private inline fun KtNamedFunction.hasAnnotation(annotationFqName: FqName): Boolean =
    KotlinPsiHeuristics.hasAnnotation(this, annotationFqName)

internal inline val KtNamedFunction.fqNameString: String
    get() = fqName!!.asString()


private object KtNamedFunctionExtensions {
    val LOG = thisLogger()
}
