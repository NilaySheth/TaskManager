package com.taskmanager.base.mvi.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.taskmanager.base.mvi.MviView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestFragment : Fragment(), MviView<TestViewState, TestSingleViewEvent> {
    val viewModel: TestActivityViewModel by viewModels<TestActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.bindMviView(viewLifecycleOwner, this)
        viewModel.loadData()
        return LinearLayout(context)
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
        // handle single events like toast, navigation(open new activity), analyticso
    }
}