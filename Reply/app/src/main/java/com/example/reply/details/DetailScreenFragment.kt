package com.example.reply.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.reply.Destination
import com.example.reply.ReplyApp
import com.example.reply.ReplyApplication
import com.example.reply.ui.ReplyTheme

@ExperimentalMaterialApi
class DetailScreenFragment : Fragment() {
    @ExperimentalLazyDsl
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
                        destination = Destination.EmailDetail(
                            index = arguments?.getLong("index") ?: 0
                        ),
                        navigationController = findNavController()
                    )
                }
            }

        }
    }
}