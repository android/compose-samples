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

package com.example.compose.jetchat.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.Recomposer
import androidx.compose.State
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.ui.core.setContent
import androidx.ui.livedata.observeAsState
import com.example.compose.jetchat.JetChatProfileTheme
import com.example.compose.jetchat.JetChatTheme
import com.example.compose.jetchat.R

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Consider using safe args plugin
        val userId = arguments?.getString("userId")
        viewModel.setUserId(userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FrameLayout(requireContext()).apply {

            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.profile_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent(Recomposer.current()) {
                viewModel.userData.observeAsState().value.let { userData: ProfileScreenState? ->
                    if (userData == null) {
                        JetChatTheme {
                            ProfileError()
                        }
                    } else {
                        JetChatProfileTheme(userData.isMe()) {
                            ProfileScreen(userData = userData)
                        }
                    }
                }
            }
        }
    }
}
