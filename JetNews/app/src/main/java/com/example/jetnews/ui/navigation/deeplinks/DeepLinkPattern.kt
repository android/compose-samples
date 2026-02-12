package com.example.jetnews.ui.navigation.deeplinks

import android.net.Uri
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.CompositeDecoder
import java.io.Serializable

/**
 * Parse a URI and store its metadata in an easily readable format
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
data class DeepLinkPattern<T : NavKey>(
    val serializer: KSerializer<T>,
    val uriPattern: Uri,
) {

    // The pattern for arguments is {argument_name}
    private val argumentPattern = Regex("\\{(.+?)\\}")

    // TODO make these lazy
    // TODO donturner - Lazy instantiation of a malformed deeplink URI will cause the app to crash. Better to instantiate here and have the app
    //  crash on startup.
    /**
     * Parse the URI path into a list of [PathSegment]s
     *
     * order matters here - path segments need to match in value and order when matching
     * requested deeplink to supported deeplink
     */
    val pathSegments: List<PathSegment> = uriPattern.pathSegments.map { segment ->
        val pathArgument = argumentPattern.find(segment)
        if (pathArgument != null) {
            val argumentName = pathArgument.groups[1]?.value ?: error("Could not extract argument name from segment: $segment")
            val elementIndex = serializer.descriptor.getElementIndex(argumentName)
            if (elementIndex == CompositeDecoder.UNKNOWN_NAME) {
                error("Path parameter '$argumentName' defined in the DeepLink $uriPattern does not exist in the Serializable class " +
                        "'${serializer.descriptor.serialName}'.")
            }
            val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
            PathSegment(argumentName, true, getTypeParser(elementDescriptor.kind))
        } else {
            PathSegment(segment, false, getTypeParser(PrimitiveKind.STRING))
        }
    }

    /**
     * Parse supported queries into a map of queryParameterNames to [TypeParser]
     *
     * This will be used later on to parse a provided query value into the correct KType
     */
    val queryValueParsers: Map<String, TypeParser> =
        uriPattern.queryParameterNames.associateWith { paramName ->
            val elementIndex = serializer.descriptor.getElementIndex(paramName)
            if (elementIndex == CompositeDecoder.UNKNOWN_NAME) {
                throw IllegalArgumentException(
                    "Query parameter '$paramName' defined in the DeepLink $uriPattern does not exist in the Serializable class '${serializer.descriptor.serialName}'.",
                )
            }
            val elementDescriptor = serializer.descriptor.getElementDescriptor(elementIndex)
            getTypeParser(elementDescriptor.kind)
        }

    /**
     * A segment of a deeplink URI.
     *
     * @param name - The name of the segment.
     * @param isArgument - `true` if this segment is an argument.
     * @param parser - the `TypeParser` for this segment.
     */
    class PathSegment(
        val name: String,
        val isArgument: Boolean,
        val parser: TypeParser,
    )
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
