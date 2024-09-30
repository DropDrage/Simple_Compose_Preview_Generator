package com.dropdrage.simpleComposePreviewGenerator.utils.extension.psi

import org.jetbrains.kotlin.idea.base.utils.fqname.fqName
import org.jetbrains.kotlin.idea.search.usagesSearch.descriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtParameter

val KtParameter.typeFqName: FqName?
    get() = this.descriptor?.returnType?.fqName
