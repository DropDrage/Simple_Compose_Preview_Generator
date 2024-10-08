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
