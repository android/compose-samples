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

package com.example.compose.jetchat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.profile.ProfileViewModel

/**
 * Main activity for the app.
 */
class NavActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var drawerShouldBeOpen by mutableStateOf(false)
    private var drawerShouldBeClosed by mutableStateOf(false)

    private var isDrawerOpen by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val state = rememberScaffoldState()

            onCommit(drawerShouldBeOpen, drawerShouldBeClosed) {
                if (drawerShouldBeOpen) {
                    state.drawerState.open {
                        drawerShouldBeOpen = false
                    }
                }
                if (drawerShouldBeClosed) {
                    state.drawerState.close {
                        drawerShouldBeClosed = false
                    }
                }
            }

            onCommit(state.drawerState.isOpen) {
                Log.d("jalc", state.drawerState.isOpen.toString())
                isDrawerOpen = state.drawerState.isOpen
            }

            Scaffold(
                drawerContent = {
                    JetchatDrawer()
                },
                scaffoldState = state
            ) {

                val context = ContextAmbient.current

                AndroidView( { LayoutInflater.from(context)
                    .inflate(R.layout.content_main, FrameLayout(context), false) }
                )

            }
        }

        viewModel.openDrawerEvent.observe(this) {
            drawerShouldBeOpen = true
        }
    }



//
//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var drawerLayout: DrawerLayout
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        drawerLayout = findViewById(R.id.drawer_layout)
//        val navView: NavigationView = findViewById(R.id.nav_view)
//        val navController = findNavController(R.id.nav_host_fragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home,
//                R.id.nav_profile
//            ),
//            drawerLayout
//        )
//        navView.setupWithNavController(navController)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }
//
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
//
    /**
     * Back closes drawer if open.
     */
    override fun onBackPressed() {
        Log.d("jalc", "jalc")
        if (isDrawerOpen) {
            drawerShouldBeClosed = true
        } else {
            super.onBackPressed()
        }
    }
}
