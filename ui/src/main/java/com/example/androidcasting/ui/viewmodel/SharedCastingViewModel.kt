package com.example.androidcasting.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcasting.core.utils.Result
import com.example.androidcasting.domain.model.CodecInfo
import com.example.androidcasting.domain.model.MediaItem
import com.example.androidcasting.domain.repository.CastingTarget
import com.example.androidcasting.domain.repository.CodecCompatibility
import com.example.androidcasting.domain.usecase.AnalyseCodecUseCase
import com.example.androidcasting.domain.usecase.CastMediaUseCase
import com.example.androidcasting.domain.usecase.CheckCompatibilityUseCase
import com.example.androidcasting.domain.usecase.GetMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.ObserveCastingTargetsUseCase
import com.example.androidcasting.domain.usecase.RefreshMediaLibraryUseCase
import com.example.androidcasting.domain.usecase.StopCastingUseCase
import com.example.androidcasting.player.PreviewPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class SharedCastingViewModel(
    private val getMediaLibraryUseCase: GetMediaLibraryUseCase,
    private val refreshMediaLibraryUseCase: RefreshMediaLibraryUseCase,
    private val observeCastingTargetsUseCase: ObserveCastingTargetsUseCase,
    private val analyseCodecUseCase: AnalyseCodecUseCase,
    private val checkCompatibilityUseCase: CheckCompatibilityUseCase,
    private val castMediaUseCase: CastMediaUseCase,
    private val stopCastingUseCase: StopCastingUseCase,
    private val previewPlayerManager: PreviewPlayerManager
) : ViewModel() {

    private val _media = MutableStateFlow<List<MediaItem>>(emptyList())
    val media: StateFlow<List<MediaItem>> = _media.asStateFlow()

    private val _devices = MutableStateFlow<List<CastingTarget>>(emptyList())
    val devices: StateFlow<List<CastingTarget>> = _devices.asStateFlow()

    private val _selectedMedia = MutableStateFlow<MediaItem?>(null)
    val selectedMedia: StateFlow<MediaItem?> = _selectedMedia.asStateFlow()

    private val _castingState = MutableStateFlow(CastingUiState())
    val castingState: StateFlow<CastingUiState> = _castingState.asStateFlow()

    val previewPlayer = previewPlayerManager.player

    init {
        viewModelScope.launch { refreshMediaLibraryUseCase() }
        observeMediaLibrary()
        observeDevices()
    }

    fun refreshLibrary() {
        viewModelScope.launch { refreshMediaLibraryUseCase() }
    }

    private fun observeMediaLibrary() {
        viewModelScope.launch {
            getMediaLibraryUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _media.value = result.data
                        _castingState.update { it.copy(errors = emptyList()) }
                    }
                    is Result.Error -> _castingState.update { it.copy(errors = listOf(result.throwable.message ?: "Unknown error")) }
                    Result.Loading -> Unit
                }
            }
        }
    }

    private fun observeDevices() {
        viewModelScope.launch {
            observeCastingTargetsUseCase().collect { _devices.value = it }
        }
    }

    fun selectMedia(mediaItem: MediaItem) {
        _selectedMedia.value = mediaItem
        _castingState.update { it.copy(selectedMedia = mediaItem) }
        previewPlayerManager.preparePreview(mediaItem.uri)
        viewModelScope.launch {
            val codecInfo = analyseCodecUseCase(mediaItem)
            val enrichedMedia = mediaItem.copy(codecInfo = codecInfo)
            _selectedMedia.value = enrichedMedia
            _castingState.update { state ->
                val compatibility = state.selectedTarget?.let { target ->
                    checkCompatibilityUseCase(target.id, codecInfo)
                } ?: checkCompatibilityUseCase(DEFAULT_TARGET, codecInfo)
                state.copy(
                    selectedMedia = enrichedMedia,
                    codecInfo = codecInfo,
                    compatibility = compatibility,
                    warnings = compatibility.reasons
                )
            }
        }
    }

    fun selectTarget(target: CastingTarget) {
        _castingState.update { it.copy(selectedTarget = target) }
        val codecInfo = _castingState.value.codecInfo
        if (codecInfo != null) {
            viewModelScope.launch {
                val compatibility = checkCompatibilityUseCase(target.id, codecInfo)
                _castingState.update { it.copy(compatibility = compatibility, warnings = compatibility.reasons) }
            }
        }
    }

    fun prepareCasting() {
        val media = _castingState.value.selectedMedia ?: return
        val target = _castingState.value.selectedTarget ?: return
        viewModelScope.launch {
            castMediaUseCase(target, media)
            val compatibility = _castingState.value.compatibility
            _castingState.update {
                it.copy(
                    isCasting = true,
                    warnings = compatibility?.reasons ?: emptyList()
                )
            }
        }
    }

    fun stopCasting() {
        viewModelScope.launch {
            stopCastingUseCase()
            _castingState.update { it.copy(isCasting = false) }
            previewPlayerManager.player.stop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        previewPlayerManager.release()
    }

    companion object {
        private const val DEFAULT_TARGET = "local"
    }
}

data class CastingUiState(
    val selectedMedia: MediaItem? = null,
    val selectedTarget: CastingTarget? = null,
    val codecInfo: CodecInfo? = null,
    val compatibility: CodecCompatibility? = null,
    val isCasting: Boolean = false,
    val warnings: List<String> = emptyList(),
    val errors: List<String> = emptyList()
)
