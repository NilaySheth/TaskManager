package com.taskmanager.ui.tasks.create

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.base.BaseBottomSheetDialog
import com.taskmanager.base.mvi.MviView
import com.taskmanager.base.utils.getTextFromTiet
import com.taskmanager.base.utils.hide
import com.taskmanager.base.utils.show
import com.taskmanager.base.utils.showToast
import com.taskmanager.base.utils.snack
import com.taskmanager.databinding.ActivityCreateTaskBinding

class CreateTaskScreen : BaseActivity(),
    MviView<CreateTaskViewState, CreateTaskSingleViewEvent> {
    private lateinit var binding: ActivityCreateTaskBinding
    private val createTaskViewModel by viewModels<CreateTaskViewModel>()

    companion object {
        fun createIntent(
            context: Context,
        ): Intent {
            return Intent(context, CreateTaskScreen::class.java).apply {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createTaskViewModel.bindMviView(this@CreateTaskScreen, this)
        setupUI()
        handleBackPress()
    }

    private fun setupUI() {
        setupToolbarMenu()
        binding.mcvStatus.setOnClickListener {
            openBSToSelectStatus(this@CreateTaskScreen)
        }
    }

    private fun validateAndCreateTask() {
        if (validateData()) {
            createTask()
        }
    }

    private fun createTask() {
        createTaskViewModel.createTask(
            binding.tietTitle.getTextFromTiet(),
            binding.tietDesc.getTextFromTiet(),
            binding.tvStatus.text.toString()
        )
    }

    private fun validateData(): Boolean {
        disableErrors()
        if (binding.tietTitle.getTextFromTiet().isBlank()) {
            binding.tilTitle.error = getString(R.string.validation_create_task_title)
            return false
        }
        if (binding.tietDesc.getTextFromTiet().isBlank()) {
            binding.tilDesc.error = getString(R.string.validation_create_task_desc)
            return false
        }
        if (binding.tvStatus.text == getString(R.string.create_task_hint_status)) {
            binding.root.snack(
                getString(R.string.validation_create_task_status),
                Snackbar.LENGTH_LONG,
                anchorView = binding.tvStatus
            )

            return false
        }
        return true
    }

    private fun disableErrors() {
        binding.tilTitle.error = null
        binding.tilDesc.error = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun openBSToSelectStatus(context: Context) {
        val typePickerDialog = BaseBottomSheetDialog(context)
        typePickerDialog.setContentView(R.layout.bs_select_status)
        val tvTodo: TextView = typePickerDialog.findViewById(R.id.tvTodo)!!
        val tvInProgress: TextView = typePickerDialog.findViewById(R.id.tvInProgress)!!
        val tvDone: TextView = typePickerDialog.findViewById(R.id.tvDone)!!

        tvTodo.setOnClickListener {
            binding.tvStatus.text = getString(R.string.bs_status_type_todo)
            binding.mcvStatus.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.statusTodo
                )
            )
            typePickerDialog.dismiss()
        }
        tvInProgress.setOnClickListener {
            binding.tvStatus.text = getString(R.string.bs_status_type_in_progress)
            binding.mcvStatus.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.statusInProgress
                )
            )
            typePickerDialog.dismiss()
        }
        tvDone.setOnClickListener {
            binding.tvStatus.text = getString(R.string.bs_status_type_done)
            binding.mcvStatus.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.statusDone
                )
            )
            typePickerDialog.dismiss()
        }

        typePickerDialog.show()
    }

    private fun setupToolbarMenu() {
        setSupportActionBar(binding.tbCreateTask)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
            this.title = getString(R.string.screen_name_create_task)
        }
        binding.tbCreateTask.setNavigationOnClickListener {
            this@CreateTaskScreen.onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_create -> validateAndCreateTask()
        }
        return true
    }

    override fun handleViewState(state: CreateTaskViewState) {
        if (state.loading) {
            binding.progressBar.root.show()
        } else {
            binding.progressBar.root.hide()
        }
    }

    override fun handleSingleViewEvent(event: CreateTaskSingleViewEvent) {
        when (event) {
            is CreateTaskSingleViewEvent.TaskCreatedSuccessfully -> {
                showToast(
                    getString(R.string.create_task_success),
                    Snackbar.LENGTH_LONG
                )
                this@CreateTaskScreen.onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        })
    }
}