/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.R
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme

class ConversationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(context = requireContext()).apply {
            setContent {
                Providers(AmbientBackPressedDispatcher provides requireActivity()) {
                    JetchatTheme {
                        ConversationContent(
                            uiState = exampleUiState,
                            navigateToProfile = { user ->
                                // Click callback
                                val bundle = bundleOf("userId" to user)
                                findNavController().navigate(
                                    R.id.nav_profile,
                                    bundle
                                )
                            },
                            onNavIconPressed = {
                                // TODO: Replace with Scaffold
                                (activity as? NavActivity)?.openDrawer()
                            }
                        )
                    }
                }
            }
        }
    }
}
