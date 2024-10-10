/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.resolveMainReference
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

internal inline val KtExpression.resolveReferencedPsiElement: PsiElement?
    get() = referenceExpression()?.resolveMainReference()
