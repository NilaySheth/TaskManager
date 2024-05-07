package com.taskmanager.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.data.local.prefs.UserPreferences
import com.taskmanager.ui.tasks.auth.LoginScreen
import com.taskmanager.ui.tasks.list.TaskListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { true }
        splashScreen.setOnExitAnimationListener {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
        if (UserPreferences.isUserLoggedIn()) {
            goToTaskListScreen()
        } else {
            goToLoginScreen()
        }
    }

    private fun goToTaskListScreen() {
        Intent(applicationContext, TaskListActivity::class.java).apply {
            startActivity(this)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }

    private fun goToLoginScreen() {
        Intent(applicationContext, LoginScreen::class.java).apply {
            startActivity(this)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}