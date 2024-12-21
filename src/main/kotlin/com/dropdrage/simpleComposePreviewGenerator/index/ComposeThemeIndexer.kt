/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.index

import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Classes
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.fqNameString
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.shortNameStringSafe
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

internal object ComposeThemeIndexer : DataIndexer<String, ThemeIndexValue, FileContent> {

    const val THEMES_KEY = "Theme"

    private val LOG = thisLogger()


    override fun map(fileContent: FileContent): Map<String, ThemeIndexValue> = when (fileContent.fileType) {
        KotlinFileType.INSTANCE -> {
            val map = mutableMapOf<String, MutableList<String>>()
            val visitor = ComposeThemeVisitor {
                map.getOrPut(THEMES_KEY, ::mutableListOf) += it
            }
            fileContent.psiFile.accept(visitor) // ToDo compare performance with manual
            map
        }

        else -> emptyMap()
    }


    private class ComposeThemeVisitor(
        private val consumer: (String) -> Unit,
    ) : KtTreeVisitorVoid() {

        override fun visitNamedFunction(function: KtNamedFunction) {
            super.visitNamedFunction(function)

            LOG.debug("Visit: $function")
            if (function.isComposableTheme()) {
                consumer(function.fqNameString)
            }
        }

        private fun KtNamedFunction.isComposableTheme(): Boolean =
            name?.endsWith(THEMES_KEY) == true
                && canBeComposableTheme(annotationEntries)
                && anyDescendantOfType<KtCallExpression> {
                it.calleeExpression?.text == Classes.Compose.Function.MaterialTheme.SHORT_NAME
            }

        private fun canBeComposableTheme(annotationEntries: List<KtAnnotationEntry>): Boolean {
            var isComposableFunction = false
            for (annotation in annotationEntries) {
                val annotationShortName = annotation.shortNameStringSafe
                if (!isComposableFunction && annotationShortName == Classes.Compose.Annotation.Composable.SHORT_NAME) {
                    isComposableFunction = true
                } else if (annotationShortName == Classes.Compose.Annotation.Preview.SHORT_NAME) {
                    LOG.debug("//// return false")
                    return false
                }
            }

            LOG.debug("//// $isComposableFunction")
            return isComposableFunction
        }

    }

}
