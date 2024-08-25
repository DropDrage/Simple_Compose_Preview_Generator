package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Creates a new line only unlike [KtPsiFactory.createNewLine] which creates a new line with whitespace.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun KtPsiFactory.createOnlyNewLine() = createWhiteSpace("\n")
