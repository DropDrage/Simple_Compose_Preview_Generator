package com.dropdrage.simpleComposePreviewGenerator.utils.i18n

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

object SimpleComposePreviewGeneratorBundle {

    private const val BUNDLE_FQN = "messages.SimpleComposePreviewGeneratorBundle"

    private val BUNDLE = DynamicBundle(SimpleComposePreviewGeneratorBundle::class.java, BUNDLE_FQN)


    fun message(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String): String = BUNDLE.getMessage(key)

    @Nls
    fun lazyMessage(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String): () -> String =
        BUNDLE.getLazyMessage(key)::get

}
