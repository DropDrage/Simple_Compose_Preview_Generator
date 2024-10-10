/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

internal class AfterTargetPreviewWriter : BasePreviewWriter(LOG, false) {

    override fun KtFile.addPreview(target: KtElement, preview: KtElement): PsiElement = addAfter(preview, target)


    companion object {
        private val LOG = logger<AfterTargetPreviewWriter>()
    }

}
