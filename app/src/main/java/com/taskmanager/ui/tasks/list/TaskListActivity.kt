package com.taskmanager.ui.tasks.list

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.base.utils.FragmentUtils
import com.taskmanager.databinding.ActivityTaskListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        openTasksFragment()
    }

    private fun openTasksFragment() {
        val fragment = TaskListFragment.getInstance()
        FragmentUtils.addFragmentToContainer(this, fragment, R.id.fragment_container, false)
    }
}