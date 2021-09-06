/*
 * Copyright 2021 The Android Open Source Project
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

package com.example.jetnews.model

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.jetnews.data.posts.impl.post1
import com.example.jetnews.data.posts.impl.post2
import com.example.jetnews.data.posts.impl.post3
import com.example.jetnews.data.posts.impl.post4
import com.example.jetnews.data.posts.impl.post5

/**
 * Provides sample [Post] instances for Composable Previews.
 *
 * When creating a Composable Preview using @Preview, you can pass sample data
 * by annotating a parameter with @PreviewParameter:
 *
 * ```
 * @Preview
 * @Composable
 * fun MyPreview(@PreviewParameter(PostPreviewParameterProvider::class, limit = 2) post: Post) {
 *   MyComposable(post)
 * }
 * ```
 *
 * In this simple app we just return the hard-coded posts. When the app
 * would be more complex - e.g. retrieving the posts from a server - this would
 * be the right place to instantiate dummy instances.
 */
class PostPreviewParameterProvider : PreviewParameterProvider<Post> {
    override val values = sequenceOf(
        post1, post2, post3, post4, post5
    )
}
