package com.taskmanager.ui.tasks.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.base.utils.FragmentUtils
import com.taskmanager.data.local.prefs.UserPreferences
import com.taskmanager.databinding.ActivityTaskListBinding
import com.taskmanager.ui.tasks.auth.LoginScreen
import com.taskmanager.ui.tasks.create.CreateTaskScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListScreen : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding

    companion object {
        fun createIntent(
            context: Context,
        ): Intent {
            return Intent(context, TaskListScreen::class.java).apply {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        setupToolbarMenu()
        openTasksFragment()
    }

    private fun setupToolbarMenu() {
        setSupportActionBar(binding.tbTaskList)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(true)
            this.title = getString(R.string.screen_name_task_list)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add ->  goToCreateTaskScreen()
            R.id.action_logout -> logoutUser()
        }
        return true
    }

    private fun logoutUser() {
        Firebase.auth.signOut()
        UserPreferences.userLogout()
        goToLoginScreen()
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

    private fun goToLoginScreen() {
        LoginScreen.createIntent(applicationContext).apply {
            startActivity(this)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}