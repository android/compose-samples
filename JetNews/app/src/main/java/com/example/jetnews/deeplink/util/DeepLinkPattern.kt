/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.deeplink.util

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import java.io.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.CompositeDecoder

/**
 * Parse a supported deeplink and stores its metadata as a easily readable format
 *
 * The following notes applies specifically to this particular sample implementation:
 *
 * The supported deeplink is expected to be built from a serializable backstack key [T] that
 * supports deeplink. This means that if this deeplink contains any arguments (path or query),
 * the argument name must match any of [T] member field name.
 *
 * One [DeepLinkPattern] should be created for each supported deeplink. This means if [T]
 * supports two deeplink patterns:
 * ```
 *  val deeplink1 = www.nav3recipes.com/home
 *  val deeplink2 = www.nav3recipes.com/profile/{userId}
 *  ```
 * Then two [DeepLinkPattern] should be created
 * ```
 * val parsedDeeplink1 = DeepLinkPattern(T.serializer(), deeplink1)
 * val parsedDeeplink2 = DeepLinkPattern(T.serializer(), deeplink2)
 * ```
 *
 * This implementation assumes a few things:
 * 1. all path arguments are required/non-nullable - partial path matches will be considered a non-match
 * 2. all query arguments are optional by way of nullable/has default value
 *
 * @param T the backstack key type that supports the deeplinking of [uriPattern]
 * @param serializer the serializer of [T]
 * @param uriPattern the supported deeplink's uri pattern, i.e. "abc.com/home/{pathArg}"
 */
class DeepLinkPattern<T : NavKey>(val serializer: KSerializer<T>, val uriPattern: Uri) {
    /**
     * Help differentiate if a path segment is an argument or a static value
     */
    private val regexPatternFillIn = Regex("\\{(.+?)\\}")

    // TODO make these lazy
    /**
     * parse the path into a list of [PathSegment]
     *
     * order matters here - path segments need to match in value and order when matching
     * requested deeplink to supported deeplink
     */
    val pathSegments: List<PathSegment> = buildList {
        uriPattern.pathSegments.forEach { segment ->
            // first, check if it is a path arg
            var result = regexPatternFillIn.find(segment)
            if (result != null) {
                // if so, extract the path arg name (the string value within the curly braces)
                val argName = result.groups[1]!!.value
                // from [T], read the primitive type of this argument to get the correct type parser
                val elementIndex = serializer.descriptor.getElementIndex(argName)
                if (elementIndex == CompositeDecoder.UNKNOWN_NAME) {
                    throw IllegalArgumentException(
                        "Path parameter '{$argName}' defined in the DeepLink $uriPattern does not exist in the Serializable class '${serializer.descriptor.serialName}'.",
                    )
                }

                val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
                // finally, add the arg name and its respective type parser to the map
                add(PathSegment(argName, true, getTypeParser(elementDescriptor.kind)))
            } else {
                // if its not a path arg, then its just a static string path segment
                add(PathSegment(segment, false, getTypeParser(PrimitiveKind.STRING)))
            }
        }
    }

    /**
     * Parse supported queries into a map of queryParameterNames to [TypeParser]
     *
     * This will be used later on to parse a provided query value into the correct KType
     */
    val queryValueParsers: Map<String, TypeParser> = buildMap {
        uriPattern.queryParameterNames.forEach { paramName ->
            val elementIndex = serializer.descriptor.getElementIndex(paramName)
            // Ignore static query parameters that are not in the Serializable class
            if (elementIndex != CompositeDecoder.UNKNOWN_NAME) {
                val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
                this[paramName] = getTypeParser(elementDescriptor.kind)
            }
        }
    }

    /**
     * Metadata about a supported path segment
     */
    class PathSegment(val stringValue: String, val isParamArg: Boolean, val typeParser: TypeParser)
}

/**
 * Parses a String into a Serializable Primitive
 */
private typealias TypeParser = (String) -> Serializable

private fun getTypeParser(kind: SerialKind): TypeParser {
    return when (kind) {
        PrimitiveKind.STRING -> Any::toString

        PrimitiveKind.INT -> String::toInt

        PrimitiveKind.BOOLEAN -> String::toBoolean

        PrimitiveKind.BYTE -> String::toByte

        PrimitiveKind.CHAR -> String::toCharArray

        PrimitiveKind.DOUBLE -> String::toDouble

        PrimitiveKind.FLOAT -> String::toFloat

        PrimitiveKind.LONG -> String::toLong

        PrimitiveKind.SHORT -> String::toShort

        else -> throw IllegalArgumentException(
            "Unsupported argument type of SerialKind:$kind. The argument type must be a Primitive.",
        )
    }
}
