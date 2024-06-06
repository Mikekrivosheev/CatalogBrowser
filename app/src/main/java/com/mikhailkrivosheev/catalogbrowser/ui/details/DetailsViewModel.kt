package com.mikhailkrivosheev.catalogbrowser.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikhailkrivosheev.catalogbrowser.data.ResultData
import com.mikhailkrivosheev.catalogbrowser.domain.Repository
import com.mikhailkrivosheev.catalogbrowser.ui.itemIdArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId = checkNotNull(savedStateHandle.get<String>(itemIdArg))

    private val mutableStateFlow = MutableStateFlow(DetailsState())
    val stateFlow = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            when (val itemResult = repository.getItemById(itemId)) {
                is ResultData.Success -> mutableStateFlow.update { it.copy(catalogItem = itemResult.data) }
                is ResultData.Exception -> mutableStateFlow.update { it.copy(errorMessage = itemResult.e.message.orEmpty()) }
            }
        }
    }

    fun onErrorMessageShown() {
        mutableStateFlow.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

}