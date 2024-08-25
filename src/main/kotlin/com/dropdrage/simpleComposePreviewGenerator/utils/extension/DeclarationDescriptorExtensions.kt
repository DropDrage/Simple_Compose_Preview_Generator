package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

internal inline val DeclarationDescriptor.fqNameSafeString: String get() = fqNameSafe.asString()


@Suppress("NOTHING_TO_INLINE")
internal inline fun DeclarationDescriptorWithVisibility.isAccessibleFrom(
    fromDescriptor: DeclarationDescriptor,
): Boolean = DescriptorVisibilities.isVisible(
    null,
    this,
    fromDescriptor,
    false,
)
