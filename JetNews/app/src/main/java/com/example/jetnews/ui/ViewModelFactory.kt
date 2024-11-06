package com.example.jetnews.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(
    private val context: Context,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        try {
            // Use reflection to find a constructor that accepts Context
            val constructor = modelClass.getConstructor(Context::class.java)
            constructor.newInstance(context)
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException("ViewModel $modelClass does not have a constructor that accepts Context")
        } catch (e: Exception) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
}
