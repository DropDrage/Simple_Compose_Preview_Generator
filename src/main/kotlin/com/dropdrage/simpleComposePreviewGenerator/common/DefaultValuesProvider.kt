package com.dropdrage.simpleComposePreviewGenerator.common

import com.dropdrage.simpleComposePreviewGenerator.config.ConfigService
import com.dropdrage.simpleComposePreviewGenerator.config.listener.PreviewGenerationSettingsChangeListener
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.Constants.FUNCTION_ARGUMENTS_SEPARATOR
import com.dropdrage.simpleComposePreviewGenerator.utils.constant.FqNameStrings
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.classNameString
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.fqNameSafeString
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.fqNameString
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.isPrimitiveArray
import com.dropdrage.simpleComposePreviewGenerator.utils.extension.isString
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import org.jetbrains.kotlin.builtins.isFunctionOrKFunctionTypeWithAnySuspendability
import org.jetbrains.kotlin.parcelize.serializers.matchesFqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isArrayOrNullableArray
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isUnit
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.isChar
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jetbrains.kotlinx.serialization.compiler.resolve.enumEntries
import org.jetbrains.kotlinx.serialization.compiler.resolve.toClassDescriptor

private typealias FqNameString = String
private typealias Value = String

@Suppress("NOTHING_TO_INLINE")
internal object DefaultValuesProvider : PreviewGenerationSettingsChangeListener {

    private val LOG = thisLogger()

    //region Settings
    private val USE_EMPTY_ARRAY_SETTING: Boolean
        get() = ConfigService.config.isEmptyBuilderForArrayEnabled
    private val USE_EMPTY_SEQUENCE_SETTING: Boolean
        get() = ConfigService.config.isEmptyBuilderForSequenceEnabled
    var SPACED_EMPTY_LAMBDA_SETTING = true
    var USE_FIRST_ENUM_SETTING = true // ToDo sounds useless

    private val useNull: Boolean
        get() = ConfigService.config.isFillNullableWithNullsEnabled
    var useNullForPrimitives = true

    //endregion

    private const val JAVA_UTIL_PACKAGE = "java.util"
    private const val KOTLIN_COLLECTIONS_PACKAGE = "kotlin.collections"

    private const val KOTLIN_SEQUENCES_PACKAGE = "kotlin.sequences"
    private const val KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE = "kotlinx.collections.immutable"

    private const val KOTLIN_COLLECTIONS_ITERABLE = "$KOTLIN_COLLECTIONS_PACKAGE.Iterable"
    private const val KOTLIN_COLLECTIONS_COLLECTION = "$KOTLIN_COLLECTIONS_PACKAGE.Collection"
    private const val KOTLIN_COLLECTIONS_LIST = "$KOTLIN_COLLECTIONS_PACKAGE.List"
    private const val KOTLIN_COLLECTIONS_SET = "$KOTLIN_COLLECTIONS_PACKAGE.Set"
    private const val KOTLIN_COLLECTIONS_MAP = "$KOTLIN_COLLECTIONS_PACKAGE.Map"

    private const val KOTLIN_SEQUENCES_SEQUENCE = "$KOTLIN_SEQUENCES_PACKAGE.Sequence"

    private const val EMPTY_ARRAY = "emptyArray()"
    private const val ARRAY_OF = "arrayOf()"

    private const val EMPTY_LIST = "emptyList()"
    private const val LIST_OF = "listOf()"
    private const val MUTABLE_LIST_OF = "mutableListOf()"
    private const val ARRAY_LIST_OF = "arrayListOf()"

    private const val EMPTY_SET = "emptySet()"
    private const val SET_OF = "setOf()"
    private const val MUTABLE_SET_OF = "mutableSetOf()"
    private const val HASH_SET_OF = "hashSetOf()"
    private const val LINKED_SET_OF = "linkedSetOf()"
    private const val SORTED_SET_OF = "sortedSetOf()"

    private const val EMPTY_MAP = "emptyMap()"
    private const val MAP_OF = "mapOf()"
    private const val MUTABLE_MAP_OF = "mutableMapOf()"
    private const val HASH_MAP_OF = "hashMapOf()"
    private const val SORTED_MAP_OF = "sortedMapOf()"
    private const val LINKED_MAP_OF = "linkedMapOf()"

    private const val EMPTY_SEQUENCE = "emptySequence()"
    private const val SEQUENCE_OF = "sequenceOf()"

    private const val PERSISTENT_LIST_OF = "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.persistentListOf()"
    private const val PERSISTENT_SET_OF = "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.persistentSetOf()"
    private const val PERSISTENT_MAP_OF = "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.persistentMapOf()"


    /**
     * Only List tree.
     */
    private val supportedIterableLists: MutableMap<FqNameString, Value> = hashMapOf(
        "$KOTLIN_COLLECTIONS_PACKAGE.MutableList" to MUTABLE_LIST_OF,
        "$KOTLIN_COLLECTIONS_PACKAGE.ArrayList" to ARRAY_LIST_OF,
        "$JAVA_UTIL_PACKAGE.AbstractList" to ARRAY_LIST_OF,
        "$JAVA_UTIL_PACKAGE.ArrayList" to ARRAY_LIST_OF,
    )

    private val supportedSets: MutableMap<FqNameString, Value> = hashMapOf(
        "$KOTLIN_COLLECTIONS_PACKAGE.MutableSet" to MUTABLE_SET_OF,
        "$KOTLIN_COLLECTIONS_PACKAGE.HashSet" to HASH_SET_OF,
        "$KOTLIN_COLLECTIONS_PACKAGE.LinkedHashSet" to LINKED_SET_OF,
        "$JAVA_UTIL_PACKAGE.HashSet" to HASH_SET_OF,
        "$JAVA_UTIL_PACKAGE.LinkedHashSet" to LINKED_SET_OF,
        "$JAVA_UTIL_PACKAGE.SortedSet" to SORTED_SET_OF,
        "$JAVA_UTIL_PACKAGE.TreeSet" to SORTED_SET_OF,
    )

    private val supportedMaps: MutableMap<FqNameString, Value> = hashMapOf(
        "$KOTLIN_COLLECTIONS_PACKAGE.MutableMap" to MUTABLE_MAP_OF,
        "$KOTLIN_COLLECTIONS_PACKAGE.HashMap" to HASH_MAP_OF,
        "$KOTLIN_COLLECTIONS_PACKAGE.LinkedHashMap" to LINKED_MAP_OF,
        "$JAVA_UTIL_PACKAGE.HashMap" to HASH_MAP_OF,
        "$JAVA_UTIL_PACKAGE.LinkedHashMap" to LINKED_MAP_OF,
        "$JAVA_UTIL_PACKAGE.SortedMap" to SORTED_MAP_OF,
    )

    private val supportedKotlinxImmutables: Map<FqNameString, Value> = hashMapOf(
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.ImmutableCollection" to PERSISTENT_LIST_OF, // persistentSetOf(), persistentHashSetOf(),
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.ImmutableList" to PERSISTENT_LIST_OF,
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.ImmutableSet" to PERSISTENT_SET_OF, // persistentHashSetOf(),
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.ImmutableMap" to PERSISTENT_MAP_OF, // persistentHashMapOf(),

        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.PersistentCollection" to PERSISTENT_LIST_OF, // persistentSetOf(), persistentHashSetOf(),
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.PersistentList" to PERSISTENT_LIST_OF,
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.PersistentSet" to PERSISTENT_SET_OF, // persistentHashSetOf(),
        "$KOTLINX_COLLECTIONS_IMMUTABLE_PACKAGE.PersistentMap" to PERSISTENT_MAP_OF, // persistentHashMapOf(),
    )


    init {
        addPossiblyEmptyBuilders()
        ApplicationManager.getApplication().messageBus.connect()
            .subscribe(PreviewGenerationSettingsChangeListener.TOPIC, this@DefaultValuesProvider)
    }


    override fun onChanged() {
        LOG.debug("Changed listener")
        addPossiblyEmptyBuilders()
    }

    private fun addPossiblyEmptyBuilders() {
        supportedIterableLists.apply {
            if (ConfigService.config.isEmptyBuilderForListEnabled) {
                put(KOTLIN_COLLECTIONS_ITERABLE, EMPTY_LIST)
                put(KOTLIN_COLLECTIONS_COLLECTION, EMPTY_LIST)
                put(KOTLIN_COLLECTIONS_LIST, EMPTY_LIST)
            } else {
                put(KOTLIN_COLLECTIONS_ITERABLE, LIST_OF)
                put(KOTLIN_COLLECTIONS_COLLECTION, LIST_OF)
                put(KOTLIN_COLLECTIONS_LIST, LIST_OF)
            }
        }
        supportedSets.apply {
            if (ConfigService.config.isEmptyBuilderForSetEnabled) {
                put(KOTLIN_COLLECTIONS_SET, EMPTY_SET)
            } else {
                put(KOTLIN_COLLECTIONS_SET, SET_OF)
            }
        }
        supportedMaps.apply {
            if (ConfigService.config.isEmptyBuilderForMapEnabled) {
                put(KOTLIN_COLLECTIONS_MAP, EMPTY_MAP)
            } else {
                put(KOTLIN_COLLECTIONS_MAP, MAP_OF)
            }
        }
    }


    fun getDefaultForType(parameterValueType: KotlinType): String = when {
        useNullForPrimitives && parameterValueType.isNullable() -> "null"

//        parameterValueType.isBoolean() -> booleanProvider.getValue()

        parameterValueType.isChar() -> "''" // somehow it isPrimitiveNumberType
//        parameterValueType.isPrimitiveNumberType() || parameterValueType.isUnsignedNumberType() ->
//            integerProvider.getValue(parameterValueType)

        parameterValueType.isString() || parameterValueType.matchesFqName(FqNameStrings.CHAR_SEQUENCE) -> "\"\""

        parameterValueType.isUnit() -> "Unit"
        parameterValueType.isNothing() -> "TODO()" // if return Nothing in lambda

        useNull && parameterValueType.isNullable() -> "null"

        parameterValueType.isEnum() -> getEnumElement(parameterValueType) // ToDo print enum class

        parameterValueType.isPrimitiveArray() -> buildPrimitiveArray(parameterValueType)
        parameterValueType.isArrayOrNullableArray() ->
            if (USE_EMPTY_ARRAY_SETTING) EMPTY_ARRAY
            else ARRAY_OF

        parameterValueType.isIterableListHasKotlinBuilder() -> buildIterableWithKotlinBuilder(parameterValueType)
        parameterValueType.isSetHasKotlinBuilder() -> buildSetWithKotlinBuilder(parameterValueType)
        parameterValueType.isMapHasKotlinBuilder() -> buildMapWithKotlinBuilder(parameterValueType)
        parameterValueType.isImmutableHasKotlinBuilder() -> buildImmutablesWithKotlinBuilder(parameterValueType)
        parameterValueType.matchesFqName(KOTLIN_SEQUENCES_SEQUENCE) ->
            if (USE_EMPTY_SEQUENCE_SETTING) EMPTY_SEQUENCE
            else SEQUENCE_OF // sequence {}, emptySequence(), generateSequence { }

        parameterValueType.isFunctionOrKFunctionTypeWithAnySuspendability -> buildLambdaDefault(parameterValueType)

        parameterValueType.matchesFqName(FqNameStrings.Compose.MODIFIER) -> FqNameStrings.Compose.MODIFIER
//        parameterValueType.isAbstract() || parameterValueType.isInterface() -> "/* TODO Unknown abstract element */"

        else -> "" //buildCustomClass(parameterValueType, functionDeclarationDescriptor)
    }


    private fun getEnumElement(parameterValueType: KotlinType): String {
        val enumEntries = parameterValueType.toClassDescriptor!!.enumEntries()
        return if (USE_FIRST_ENUM_SETTING) enumEntries.first().fqNameSafeString
        else enumEntries.last().fqNameSafeString
    }

    private fun buildPrimitiveArray(parameterValueType: KotlinType): String =
        "${parameterValueType.classNameString.snakeCaseClassName()}Of()"

    private fun String.snakeCaseClassName(): String = with(StringBuilder(this)) {
        var i = 0
        while (this[i].isUpperCase() && i < length) {
            this[i] = this[i].lowercaseChar()
            i++
        }
        toString()
    }

    private inline fun KotlinType.isIterableListHasKotlinBuilder(): Boolean =
        supportedIterableLists.containsKey(fqNameString)

    private inline fun buildIterableWithKotlinBuilder(parameterValueType: KotlinType): String =
        supportedIterableLists[parameterValueType.fqNameString]!!

    private inline fun KotlinType.isMapHasKotlinBuilder(): Boolean =
        supportedMaps.containsKey(fqNameString)

    private inline fun buildMapWithKotlinBuilder(parameterValueType: KotlinType): String =
        supportedMaps[parameterValueType.fqNameString]!!

    private inline fun KotlinType.isSetHasKotlinBuilder(): Boolean =
        supportedSets.containsKey(fqNameString)

    private inline fun buildSetWithKotlinBuilder(parameterValueType: KotlinType): String =
        supportedSets[parameterValueType.fqNameString]!!

    private inline fun KotlinType.isImmutableHasKotlinBuilder(): Boolean =
        supportedKotlinxImmutables.containsKey(fqNameString)

    private inline fun buildImmutablesWithKotlinBuilder(parameterValueType: KotlinType): String =
        supportedKotlinxImmutables[parameterValueType.fqNameString]!!

    private fun buildLambdaDefault(parameterValueType: KotlinType): String = buildString {
        append('{')

        val arguments = parameterValueType.arguments

        val inputArgumentsCount = arguments.lastIndex
        val hasParameters = inputArgumentsCount > 0
        if (hasParameters || SPACED_EMPTY_LAMBDA_SETTING) { // remove if space should be left { }
            append(' ')
        }
        repeat(inputArgumentsCount) {
            if (it != 0) { // avoids redundant "," before defaults
                append(FUNCTION_ARGUMENTS_SEPARATOR).append(' ')
            }
            append('_')
        }
        if (hasParameters) {
            append("-> ")
        }

        val returnType = arguments.last().type
        if (!returnType.isUnit()) {
            append(getDefaultForType(returnType))
        }

        append('}')
    }

}
