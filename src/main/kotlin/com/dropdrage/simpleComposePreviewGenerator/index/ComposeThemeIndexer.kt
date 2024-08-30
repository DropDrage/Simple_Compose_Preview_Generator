package com.dropdrage.simpleComposePreviewGenerator.index

import com.dropdrage.simpleComposePreviewGenerator.utils.constant.ShortNames
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.fqNameString
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.shortNameStringSafe
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.debugger.sequence.psi.callName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

internal object ComposeThemeIndexer : DataIndexer<String, ThemeIndexValue, FileContent> {

    const val THEMES_KEY = "Theme"


    override fun map(fileContent: FileContent): Map<String, ThemeIndexValue> = when (fileContent.fileType) {
        KotlinFileType.INSTANCE -> {
            val map = mutableMapOf<String, MutableList<String>>()
            val visitor = ComposeThemeVisitor {
                map.getOrPut(THEMES_KEY) { mutableListOf() } += it
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

            println("Visit: $function")
            if (function.isComposableTheme()) {
                consumer(function.fqNameString)
            }
        }

        private fun KtNamedFunction.isComposableTheme(): Boolean {
            return name?.endsWith(THEMES_KEY) == true
                && canBeComposableTheme(annotationEntries)
                && anyDescendantOfType<KtCallExpression> { it.callName() == ShortNames.Function.MATERIAL_THEME }
        }

        private fun canBeComposableTheme(annotationEntries: MutableList<KtAnnotationEntry>): Boolean {
            var isComposableFunction = false
            for (annotation in annotationEntries) {
                if (!isComposableFunction && annotation.shortNameStringSafe == ShortNames.Annotation.COMPOSABLE) {
                    isComposableFunction = true
                } else if (annotation.shortNameStringSafe == ShortNames.Annotation.PREVIEW) {
                    println("//// return false")
                    return false
                }
            }

            println("//// $isComposableFunction")
            return isComposableFunction
        }

    }

}