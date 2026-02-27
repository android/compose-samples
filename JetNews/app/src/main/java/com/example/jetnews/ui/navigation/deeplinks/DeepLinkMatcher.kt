package com.example.jetnews.ui.navigation.deeplinks

import android.net.Uri
import android.util.Log
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.KSerializer

internal class DeepLinkMatcher<T : NavKey>(
    val data: Uri,
    val pattern: DeepLinkPattern<T>
) {
    /**
     * Match a [DeepLinkRequest] to a [DeepLinkPattern].
     *
     * Returns a [DeepLinkMatchResult] if this matches the pattern, returns null otherwise
     */
    fun match(): DeepLinkMatchResult<T>? {
        if (data.scheme != pattern.uriPattern.scheme) return null
        if (!data.authority.equals(pattern.uriPattern.authority, ignoreCase = true)) return null
        if (data.pathSegments.size != pattern.pathSegments.size) return null
        // exact match (url does not contain any arguments)
        if (data == pattern.uriPattern)
            return DeepLinkMatchResult(pattern.serializer, mapOf())

        // map of query names to their instantiated objects
        val args = mutableMapOf<String, Any>()

        // match the path
        data.pathSegments
            .asSequence()
            // zip to compare the two objects side by side, order matters here so we
            // need to make sure the compared segments are at the same position within the url
            .zip(pattern.pathSegments.asSequence())
            .forEach { it ->
                // retrieve the two path segments to compare
                val requestedSegment = it.first
                val candidateSegment = it.second
                // if the potential match expects a path arg for this segment, try to parse the
                // requested segment into the expected type
                if (candidateSegment.isArgument) {
                    val parsedValue = try {
                        candidateSegment.parser.invoke(requestedSegment)
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG_LOG_ERROR, "Failed to parse path value:[$requestedSegment].", e)
                        return null
                    }
                    args[candidateSegment.name] = parsedValue
                } else if(requestedSegment != candidateSegment.name){
                    // if it's path arg is not the expected type, its not a match
                    return null
                }
            }
        // match queries (if any)
        data.queryParametersMap.forEach { query ->
            val queryName = query.key
            val queryValue = query.value
            val queryStringParser = pattern.queryValueParsers[queryName] ?: error("Could not get parser for query with name: $queryName")
            if (queryValue == null){
                error("Query value: $queryValue was null")
            } else {
                val queryParsedValue = queryStringParser.invoke(queryValue)
                args[queryName] = queryParsedValue
            }
        }
        // provide the serializer of the matching key and map of arg names to parsed arg values
        return DeepLinkMatchResult(pattern.serializer, args)
    }
}


/**
 * Created when a requested deeplink matches with a supported deeplink
 *
 * @param [T] the backstack key associated with the deeplink that matched with the requested deeplink
 * @param serializer serializer for [T]
 * @param args The map of argument name to argument value. The value is expected to have already
 * been parsed from the raw url string back into its proper KType as declared in [T].
 * Includes arguments for all parts of the uri - path, query, etc.
 * */
internal data class DeepLinkMatchResult<T : NavKey>(
    val serializer: KSerializer<T>,
    val args: Map<String, Any>
)

const val TAG_LOG_ERROR = "Nav3RecipesDeepLink"
