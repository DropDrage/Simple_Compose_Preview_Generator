/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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
