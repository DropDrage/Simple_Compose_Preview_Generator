package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.caches.resolve.resolveMainReference
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

internal inline val KtExpression.resolveReferencedPsiElement: PsiElement?
    get() = referenceExpression()?.resolveMainReference()
