package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.psi.KtDeclaration

internal inline val KtDeclaration.descriptorWithVisibility: DeclarationDescriptorWithVisibility
    get() = descriptor as DeclarationDescriptorWithVisibility
