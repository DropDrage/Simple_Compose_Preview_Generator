/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import org.jetbrains.kotlin.psi.KtElement

class FunctionWithPreview(
    val target: KtElement,
    val preview: KtElement,
    val previewWithForcedArguments: KtElement = preview,
) {
    operator fun component1() = target
    operator fun component2() = preview
    operator fun component3() = previewWithForcedArguments
}
