package com.taskmanager.ui.splash

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.ui.tasks.auth.LoginScreen
import com.taskmanager.ui.tasks.list.TaskListScreen
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
        if (Firebase.auth.currentUser != null) {
            goToTaskListScreen()
        } else {
            goToLoginScreen()
        }
    }

    private fun goToTaskListScreen() {
        TaskListScreen.createIntent(applicationContext).apply {
            startActivity(this)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
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