/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import org.jetbrains.kotlin.idea.KotlinFileType

internal class ComposeThemeInputFilter : DefaultFileTypeSpecificInputFilter(KotlinFileType.INSTANCE) {
    override fun acceptInput(file: VirtualFile): Boolean {
        if (!file.nameWithoutExtension.endsWith("Theme")) {
            return false
        }
        return super.acceptInput(file)
    }
}
