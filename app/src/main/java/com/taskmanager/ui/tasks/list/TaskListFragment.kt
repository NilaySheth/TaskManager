package com.taskmanager.ui.tasks.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.taskmanager.base.BaseFragment
import com.taskmanager.databinding.FragmentTaskListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment: BaseFragment() {

    private lateinit var binding: FragmentTaskListBinding
    companion object {
        fun getInstance(): Fragment {
            val fragment = TaskListFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        //TODO Nilay getTaskList()
    }
}