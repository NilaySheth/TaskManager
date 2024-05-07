package com.taskmanager.base

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.taskmanager.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavigationBarColor()
    }

    fun setNavigationBarColor(
        color: Int = ContextCompat.getColor(
            this,
            R.color.backgroundPrimary
        )
    ) {
        window.navigationBarColor = color
    }
}