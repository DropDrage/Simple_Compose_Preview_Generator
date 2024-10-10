/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import com.android.tools.compose.isComposableAnnotation
import com.android.tools.idea.kotlin.fqNameMatches
import com.android.tools.idea.kotlin.hasAnnotation
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Classes
import com.intellij.openapi.diagnostic.thisLogger
import org.jetbrains.kotlin.psi.KtNamedFunction

internal fun KtNamedFunction.isTargetForComposePreview(): Boolean {
    var isComposableFunction = false
    for (annotation in annotationEntries) {
        if (!isComposableFunction && annotation.isComposableAnnotation()) {
            isComposableFunction = true
        } else if (annotation.fqNameMatches(Classes.Compose.Annotation.Preview.FQ_STRING)) {
            KtNamedFunctionExtensions.LOG.debug("//// return false")
            return false
        }
    }

    KtNamedFunctionExtensions.LOG.debug("//// $isComposableFunction")
    return isComposableFunction
}

internal inline fun KtNamedFunction.isComposePreviewFunction(): Boolean =
    hasAnnotation(Classes.Compose.Annotation.Preview.CLASS_ID)

internal inline val KtNamedFunction.fqNameString: String
    get() = fqName!!.asString()


private object KtNamedFunctionExtensions {
    val LOG = thisLogger()
}
