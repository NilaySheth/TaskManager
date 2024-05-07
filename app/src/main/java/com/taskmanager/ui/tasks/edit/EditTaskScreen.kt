package com.taskmanager.ui.tasks.edit

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
import com.taskmanager.ui.tasks.list.TaskListSingleViewEvent
import com.taskmanager.ui.tasks.list.TaskListViewModel
import com.taskmanager.ui.tasks.list.TaskListViewState
import com.taskmanager.ui.tasks.list.TasksModel

class EditTaskScreen : BaseActivity(),
    MviView<TaskListViewState, TaskListSingleViewEvent> {
    private lateinit var binding: ActivityCreateTaskBinding
    private val editTaskViewModel by viewModels<TaskListViewModel>()

    companion object {
        fun createIntent(
            context: Context,
            taskModel: TasksModel?,
        ): Intent {
            return Intent(context, EditTaskScreen::class.java).apply {
                putExtra("taskModel", taskModel)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        editTaskViewModel.bindMviView(this@EditTaskScreen, this)
        setupUI()
        handleBackPress()
    }

    private fun setupUI() {
        setupToolbarMenu()
        setDataFromIntent()
        binding.mcvStatus.setOnClickListener {
            openBSToSelectStatus(this@EditTaskScreen)
        }
    }

    private fun setDataFromIntent() {
        val task = intent.getParcelableExtra<TasksModel>("taskModel")
        task?.let {
            editTaskViewModel.setTaskModel(it)
            binding.tietTitle.setText(task.title)
            binding.tietDesc.setText(task.desc)
            binding.tvStatus.text = task.status

            when (task.status) {
                getString(R.string.bs_status_type_todo) -> {
                    binding.mcvStatus.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.statusTodo
                        )
                    )
                }

                getString(R.string.bs_status_type_in_progress) -> {
                    binding.mcvStatus.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.statusInProgress
                        )
                    )
                }

                getString(R.string.bs_status_type_done) -> {
                    binding.mcvStatus.setCardBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.statusDone
                        )
                    )
                }
            }
        }
    }

    private fun validateAndEditTask() {
        if (validateData()) {
            editTask()
        }
    }

    private fun editTask() {
        val taskModel: TasksModel = editTaskViewModel.getTaskModel()!!
        taskModel.apply {
            title = binding.tietTitle.getTextFromTiet()
            desc = binding.tietDesc.getTextFromTiet()
            status = binding.tvStatus.text.toString()
        }

        editTaskViewModel.editTask(
            taskModel
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
            this.title = getString(R.string.screen_name_edit_task)
        }
        binding.tbCreateTask.setNavigationOnClickListener {
            this@EditTaskScreen.onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> validateAndEditTask()
        }
        return true
    }

    override fun handleViewState(state: TaskListViewState) {
        if (state.loading) {
            binding.progressBar.root.show()
        } else {
            binding.progressBar.root.hide()
        }
    }

    override fun handleSingleViewEvent(event: TaskListSingleViewEvent) {
        when (event) {
            is TaskListSingleViewEvent.TaskEditedSuccessfully -> {
                showToast(
                    getString(R.string.edit_task_success),
                    Snackbar.LENGTH_LONG
                )
                goBackWithSuccess()
            }

            TaskListSingleViewEvent.TaskDeleteSuccessfully -> {

            }

            is TaskListSingleViewEvent.TasksFetchedSuccessfully -> {

            }

            is TaskListSingleViewEvent.TaskFetchedSuccessfully -> {

            }
        }
    }

    private fun goBackWithSuccess() {
        intent.putExtra("Edited", true)
        setResult(RESULT_OK, intent)
        this@EditTaskScreen.onBackPressedDispatcher.onBackPressed()
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