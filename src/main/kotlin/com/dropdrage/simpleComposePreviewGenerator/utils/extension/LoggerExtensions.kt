package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import com.intellij.openapi.diagnostic.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.measureTime

@OptIn(ExperimentalContracts::class)
inline fun Logger.logTimeOnDebug(label: String, block: () -> Unit) {
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
