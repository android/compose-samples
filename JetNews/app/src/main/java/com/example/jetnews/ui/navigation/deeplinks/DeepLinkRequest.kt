package com.example.jetnews.ui.navigation.deeplinks

import android.net.Uri

/**
 * Parse the requested Uri and store it in a easily readable format
 *
 * @param uri the target deeplink uri to link to
 */
internal class DeepLinkRequest(
    val uri: Uri
) {
    /**
     * A list of path segments
     */
    val pathSegments: List<String> = uri.pathSegments

    // TODO add parsing for other Uri components, i.e. fragments, mimeType, action
}

/**
 * A map of query name to query value
 */
val Uri.queryParametersMap: Map<String, String?>
    get() = this.queryParameterNames.associateWith { this.getQueryParameter(it) }
