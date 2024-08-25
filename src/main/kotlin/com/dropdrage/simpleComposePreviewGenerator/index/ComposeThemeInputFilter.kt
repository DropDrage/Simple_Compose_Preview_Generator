package com.dropdrage.simpleComposePreviewGenerator.index

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import org.jetbrains.kotlin.idea.KotlinFileType

internal class ComposeThemeInputFilter : DefaultFileTypeSpecificInputFilter(KotlinFileType.INSTANCE) {
    override fun acceptInput(file: VirtualFile): Boolean {
        if (!file.nameWithoutExtension.endsWith("Theme")) {
            return false
        }
        return super.acceptInput(file)
    }
}
