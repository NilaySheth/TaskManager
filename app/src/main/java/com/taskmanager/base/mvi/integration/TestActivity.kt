package com.taskmanager.base.mvi.integration

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.taskmanager.base.mvi.MviView
import com.taskmanager.base.mvi.SingleViewEvent
import com.taskmanager.base.mvi.ViewState
import dagger.hilt.android.AndroidEntryPoint

data class TestViewState(val loading: Boolean, val animState: String) : ViewState

sealed interface TestSingleViewEvent : SingleViewEvent {
    object Error : TestSingleViewEvent
}

@AndroidEntryPoint
class TestActivity : AppCompatActivity(), MviView<TestViewState, TestSingleViewEvent> {
    private val viewModel by viewModels<TestActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.bindMviView(this@TestActivity, this)
        viewModel.loadData()
        supportFragmentManager.beginTransaction()
            .add(TestFragment(), TestFragment::class.java.simpleName)
            .commit()
    }

    override fun handleViewState(state: TestViewState) {
        // handle ui here
        if (state.loading) {
            // progress bar show
        } else {
            // progress bar hide
        }
    }

    override fun handleSingleViewEvent(event: TestSingleViewEvent) {
        // handle single events like toast, navigation(open new activity), analytics
    }
}