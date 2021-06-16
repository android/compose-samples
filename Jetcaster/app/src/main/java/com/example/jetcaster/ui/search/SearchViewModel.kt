package com.example.jetcaster.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.Graph
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.PodcastStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SearchViewModel(
    private val podcastStore: PodcastStore = Graph.podcastStore
) : ViewModel() {
    private val searchText = MutableStateFlow("")
    private val results = MutableStateFlow(emptyList<Podcast>())
    private val _state = MutableStateFlow(SearchViewState())
    val state: StateFlow<SearchViewState>
        get() = _state

    init {
        viewModelScope.launch {
            combine(
                searchText,
                results
            ) { searchText, results ->
                SearchViewState(searchText = searchText, results = results)
            }.collect { _state.value = it }
        }
        search(searchText.value)
    }

    private fun search(searchText: String) {
        viewModelScope.launch {
            podcastStore.podcastsWithTitle(searchText).collect {
                results.value = it
            }
        }
    }

    fun onSearchTextChanged(searchText: String) {
        this.searchText.value = searchText
        search(searchText)
    }
}

data class SearchViewState(
    val results: List<Podcast> = emptyList(),
    val searchText: String = ""
)