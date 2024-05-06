package com.taskmanager.base.mvi.integration

import androidx.lifecycle.viewModelScope
import com.taskmanager.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TestActivityViewModel @Inject constructor() :
    MviViewModel<TestViewState, TestSingleViewEvent>() {
    override fun initialState(): TestViewState {
        return TestViewState(false, "")
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                updateViewState(stateReducer = {
                    it.copy(loading = true)
                })
                delay(1000)
                updateViewState(stateReducer = {
                    it.copy(loading = false)
                })
                withContext(Dispatchers.Main) {
                    postSingleViewEvent(TestSingleViewEvent.Error)
                }
            }
        }
    }
}