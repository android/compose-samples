package com.example.reply.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.reply.Destination
import com.example.reply.R
import com.example.reply.ReplyApp
import com.example.reply.ReplyApplication
import com.example.reply.ui.ReplyTheme

@ExperimentalMaterialApi
@OptIn(ExperimentalLazyDsl::class)
class HomeScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            val appContainer = (requireActivity().application as ReplyApplication).container
            setContent {
                ReplyTheme {
                    ReplyApp(
                        appContainer = appContainer,
                        destination = Destination.HomeScreen,
                        navigationController = findNavController()
                    )
                }
            }

        }
    }
}