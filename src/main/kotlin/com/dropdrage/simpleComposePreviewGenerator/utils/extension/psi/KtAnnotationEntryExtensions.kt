/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import org.jetbrains.kotlin.psi.KtAnnotationEntry

internal inline val KtAnnotationEntry.shortNameStringSafe: String?
    get() = shortName?.asString()
