package com.taskmanager.ui.tasks.list

import android.os.Bundle
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.base.utils.FragmentUtils
import com.taskmanager.databinding.ActivityTaskListBinding
import com.taskmanager.ui.tasks.create.CreateTaskScreen
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
        setupToolbarMenu()
        binding.fabCreateTask.setOnClickListener { view ->
            goToCreateTaskScreen()
        }
        openTasksFragment()
    }

    private fun setupToolbarMenu() {
        setSupportActionBar(binding.tbTaskList)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(true)
            this.title = getString(R.string.screen_name_task_list)
        }
//        binding.tbTaskList.setNavigationOnClickListener {
//            this@TaskListActivity.onBackPressedDispatcher.onBackPressed()
//        }
    }

    private fun openTasksFragment() {
        val fragment = TaskListFragment.getInstance()
        FragmentUtils.addFragmentToContainer(this, fragment, R.id.fragment_container, false)
    }

    private fun goToCreateTaskScreen() {
        CreateTaskScreen.createIntent(applicationContext).apply {
            startActivity(this)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}