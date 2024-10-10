/*
 * SPDX-License-Identifier: MPL-2.0
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.dropdrage.simpleComposePreviewGenerator.utils.constant

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object Classes {
    object Compose {
        object Annotation {
            object Composable {
                const val SHORT_NAME = "Composable"
                const val FQ_STRING = "androidx.compose.runtime.$SHORT_NAME"
            }

            object Preview {
                const val SHORT_NAME = "Preview"
                const val PACKAGE = "androidx.compose.ui.tooling.preview"
                const val FQ_STRING = "$PACKAGE.$SHORT_NAME"

                val PACKAGE_FQ_NAME = FqName(PACKAGE)
                val CLASS_ID = ClassId(PACKAGE_FQ_NAME, Name.identifier(SHORT_NAME))
            }
        }

        object Function {
            object MaterialTheme {
                const val SHORT_NAME = "MaterialTheme"
            }
        }

        object Modifier {
            const val SHORT_NAME = "Modifier"
            const val FQ_STRING = "androidx.compose.ui.$SHORT_NAME"
            val FQ_NAME = FqName(FQ_STRING)
        }
    }

    object Android {
        object ViewModel {
            const val FQ_STRING = "androidx.lifecycle.ViewModel"
            val FQ_NAME = FqName(FQ_STRING)
        }
    }

    object Kotlin {
        object CharSequence {
            const val FQ_STRING = "kotlin.CharSequence"
        }
    }
}
