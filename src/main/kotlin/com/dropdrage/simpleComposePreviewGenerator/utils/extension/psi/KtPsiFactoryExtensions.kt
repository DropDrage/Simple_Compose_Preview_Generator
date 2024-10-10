/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Creates a new line only unlike [KtPsiFactory.createNewLine] which creates a new line with whitespace.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun KtPsiFactory.createOnlyNewLine() = createWhiteSpace("\n")
