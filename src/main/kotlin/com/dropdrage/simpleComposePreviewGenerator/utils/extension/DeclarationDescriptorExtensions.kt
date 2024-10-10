/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

internal inline val DeclarationDescriptor.fqNameSafeString: String get() = fqNameSafe.asString()


@Suppress("NOTHING_TO_INLINE")
internal inline fun DeclarationDescriptorWithVisibility.isAccessibleFrom(
    fromDescriptor: DeclarationDescriptor,
): Boolean = DescriptorVisibilities.isVisible(
    null,
    this,
    fromDescriptor,
    false,
)
