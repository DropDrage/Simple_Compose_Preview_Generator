/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.annotations.NonNls
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.measureTime

@OptIn(ExperimentalContracts::class)
inline fun Logger.logTimeOnDebug(@NonNls label: String, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    if (isDebugEnabled) {
        val time = measureTime(block)
        debug("$label time: $time")
    } else {
        block()
    }
}

@OptIn(ExperimentalContracts::class)
inline fun <R> Logger.logTimeOnDebugResulted(label: String, crossinline block: () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val result: R
    if (isDebugEnabled) {
        val time = measureTime { result = block() }
        debug("$label time: $time")
    } else {
        result = block()
    }
    return result
}
