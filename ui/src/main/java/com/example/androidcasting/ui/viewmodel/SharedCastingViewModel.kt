package com.example.androidcasting.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcasting.domain.usecase.GetMediaLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedCastingViewModel @Inject constructor(
    private val getMediaLibraryUseCase: GetMediaLibraryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CastingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMediaLibrary()
    }

    private fun loadMediaLibrary() {
        viewModelScope.launch {
            // This is where the media library would be loaded from the use case.
            // For now, we'll leave it empty.
        }
    }
}
