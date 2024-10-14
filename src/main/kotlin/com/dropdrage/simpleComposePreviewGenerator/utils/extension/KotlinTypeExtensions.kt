/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.idea.base.utils.fqname.fqName
import org.jetbrains.kotlin.types.KotlinType

internal inline val KotlinType.classNameString: String
    get() = fqName!!.classNameString
internal inline val KotlinType.fqNameString: String
    get() = fqName!!.asString()