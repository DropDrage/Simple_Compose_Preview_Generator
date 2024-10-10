/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.name.FqName

internal inline val FqName.classNameString: String get() = shortName().asString()
internal inline val FqName.packageString: String get() = parent().asString()
