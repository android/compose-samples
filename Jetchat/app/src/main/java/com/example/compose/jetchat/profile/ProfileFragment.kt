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

package com.example.compose.jetchat.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.theme.JetchatTheme

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Consider using safe args plugin
        val userId = arguments?.getString("userId")
        viewModel.setUserId(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(context = requireContext()).apply {
            setContent {
                viewModel.userData.observeAsState().value.let { userData: ProfileScreenState? ->
                    JetchatTheme {
                        if (userData == null) {
                            ProfileError()
                        } else {
                            ProfileScreen(
                                userData = userData,
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
}
