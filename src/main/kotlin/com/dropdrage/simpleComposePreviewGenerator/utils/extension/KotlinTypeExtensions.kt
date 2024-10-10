/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.idea.base.utils.fqname.fqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.types.KotlinType

private val PRIMITIVE_ARRAY_REGEX = Regex("""kotlin\.([a-zA-Z])+Array""")

internal inline val KotlinType.classNameString: String
    get() = fqName!!.classNameString
internal inline val KotlinType.fqNameString: String
    get() = fqName!!.asString()
internal inline val KotlinType.fqNamePackageString: String
    get() = fqName!!.packageString
internal inline val KotlinType.ktSourceClass: KtClass
    get() = constructor.declarationDescriptor!!.source.getPsi() as KtClass

internal fun KotlinType.isString(): Boolean {
    val fqNameString = fqNameString
    return fqNameString == "java.lang.String" || fqNameString == "kotlin.String"
}

internal fun KotlinType.isPrimitiveArray(): Boolean = PRIMITIVE_ARRAY_REGEX.matches(fqNameString)