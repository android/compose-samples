/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import android.widget.FrameLayout
import androidx.compose.Providers
import androidx.compose.Recomposer
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.ui.core.setContent
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.R
import com.example.compose.jetchat.theme.JetchatTheme

class ConversationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FrameLayout(requireContext()).apply {

            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.conversation_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent(Recomposer.current()) {
                Providers(BackPressedDispatcherAmbient provides requireActivity()) {
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
