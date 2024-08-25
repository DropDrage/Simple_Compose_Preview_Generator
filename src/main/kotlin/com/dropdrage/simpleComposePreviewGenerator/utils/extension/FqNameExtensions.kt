package com.dropdrage.simpleComposePreviewGenerator.utils.extension

import org.jetbrains.kotlin.name.FqName

internal inline val FqName.classNameString: String get() = shortName().asString()
internal inline val FqName.packageString: String get() = parent().asString()
