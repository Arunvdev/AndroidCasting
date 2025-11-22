package com.example.androidcasting.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcasting.domain.model.CastingTarget
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.usecase.CastMediaUseCase
import com.example.androidcasting.domain.usecase.GetMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.ObserveCastingTargetsUseCase
import com.example.androidcasting.domain.usecase.StopCastingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedCastingViewModel @Inject constructor(
    private val getMediaLibraryUseCase: GetMediaLibraryUseCase,
    private val observeCastingTargetsUseCase: ObserveCastingTargetsUseCase,
    private val castMediaUseCase: CastMediaUseCase,
    private val stopCastingUseCase: StopCastingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CastingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMediaLibrary()
        observeTargets()
    }

    private fun loadMediaLibrary() {
        viewModelScope.launch {
            val media = getMediaLibraryUseCase()
            _uiState.update { it.copy(media = media) }
        }
    }

    private fun observeTargets() {
        viewModelScope.launch {
            observeCastingTargetsUseCase().collect { targets ->
                _uiState.update { it.copy(availableTargets = targets) }
            }
        }
    }

    fun selectMedia(mediaItem: MediaItem) {
        _uiState.update { it.copy(selectedMedia = mediaItem) }
    }

    fun selectTarget(target: CastingTarget) {
        _uiState.update { it.copy(selectedTarget = target) }
    }

    fun startCasting(onFinished: () -> Unit = {}) {
        val media = _uiState.value.selectedMedia
        val target = _uiState.value.selectedTarget
        if (media != null && target != null) {
            viewModelScope.launch {
                castMediaUseCase(media, target)
                _uiState.update { it.copy(isCasting = true) }
                onFinished()
            }
        }
    }

    fun stopCasting() {
        viewModelScope.launch {
            stopCastingUseCase()
            _uiState.update { it.copy(isCasting = false) }
        }
    }
}
