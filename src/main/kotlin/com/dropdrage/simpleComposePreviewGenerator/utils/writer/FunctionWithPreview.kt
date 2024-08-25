package com.dropdrage.simpleComposePreviewGenerator.utils.writer

import com.intellij.psi.PsiElement

class FunctionWithPreview(
    val target: PsiElement,
    val preview: PsiElement,
) {
    operator fun component1() = target
    operator fun component2() = preview
}
