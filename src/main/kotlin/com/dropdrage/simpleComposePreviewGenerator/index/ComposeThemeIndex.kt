/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.index

import androidx.compose.compiler.plugins.kotlin.lower.fastForEach
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.isAccessibleFrom
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi.descriptorWithVisibility
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.intellij.util.io.externalizer.StringCollectionExternalizer
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.idea.base.projectStructure.scope.KotlinSourceFilterScope
import org.jetbrains.kotlin.idea.stubindex.KotlinTopLevelFunctionFqnNameIndex

internal typealias ThemeIndexValue = List<String> // ToDo Set<>

internal class ComposeThemeIndex : FileBasedIndexExtension<String, ThemeIndexValue>() {

    override fun getName(): ID<String, ThemeIndexValue> = NAME
    override fun getVersion(): Int = 0
    override fun dependsOnFileContent(): Boolean = true


    override fun getInputFilter(): FileBasedIndex.InputFilter = ComposeThemeInputFilter()

    override fun getIndexer(): DataIndexer<String, ThemeIndexValue, FileContent> = ComposeThemeIndexer

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<ThemeIndexValue> = StringCollectionExternalizer(::ArrayList)


    companion object {

        private val NAME: ID<String, ThemeIndexValue> = ID.create(ComposeThemeIndex::class.java.name)


        fun findAccessibleThemeName(
            project: Project,
            functionDeclarationDescriptor: DeclarationDescriptorWithVisibility,
        ): String? {
            val themeFq = findAccessibleThemeFq(project, functionDeclarationDescriptor) ?: return null
            return themeFq.substringAfterLast('.')
        }

        fun findAccessibleThemeFq(
            project: Project,
            functionDeclarationDescriptor: DeclarationDescriptorWithVisibility,
        ): String? {
            val kotlinSourcesScope =
                KotlinSourceFilterScope.projectSources(GlobalSearchScope.projectScope(project), project)
            val allThemes = FileBasedIndex.getInstance().getValues(
                NAME,
                ComposeThemeIndexer.THEMES_KEY,
                kotlinSourcesScope,
            )

            allThemes.fastForEach { fileThemes ->
                fileThemes.fastForEach { themeFqString ->
                    KotlinTopLevelFunctionFqnNameIndex.get(themeFqString, project, kotlinSourcesScope)
                        .forEach { themeFunction ->
                            if (themeFunction.descriptorWithVisibility.isAccessibleFrom(functionDeclarationDescriptor)) {
                                return themeFqString
                            }
                        }
                }
            }
            return null
        }

    }

}
