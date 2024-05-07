package com.taskmanager.ui.tasks.list.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.base.BaseBottomSheetDialog
import com.taskmanager.base.mvi.MviView
import com.taskmanager.base.utils.hide
import com.taskmanager.base.utils.show
import com.taskmanager.base.utils.showToast
import com.taskmanager.databinding.ActivityTaskDetailBinding
import com.taskmanager.ui.tasks.edit.EditTaskScreen
import com.taskmanager.ui.tasks.list.TaskListSingleViewEvent
import com.taskmanager.ui.tasks.list.TaskListViewModel
import com.taskmanager.ui.tasks.list.TaskListViewState
import com.taskmanager.ui.tasks.list.TasksModel

class TaskDetailScreen : BaseActivity(),
    MviView<TaskListViewState, TaskListSingleViewEvent> {
    private lateinit var binding: ActivityTaskDetailBinding
    private val taskDetailViewModel by viewModels<TaskListViewModel>()

    companion object {
        fun createIntent(
            context: Context,
            taskModel: TasksModel
        ): Intent {
            return Intent(context, TaskDetailScreen::class.java).apply {
                putExtra("taskModel", taskModel)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskDetailViewModel.bindMviView(this@TaskDetailScreen, this)
        setupUI()
        handleBackPress()
    }

    private fun setupUI() {
        setupToolbarMenu()
        intent.getParcelableExtra<TasksModel>("taskModel")?.let { updateTaskDetailData(it) }
        binding.mcvStatus.setOnClickListener {
            openBSToSelectStatus(this@TaskDetailScreen)
        }
    }

    private fun updateTaskDetailData(task: TasksModel) {
        task?.let {
            taskDetailViewModel.setTaskModel(it)
            binding.tvTitle.text = task.title
            binding.tvDesc.text = task.desc
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

    private fun setupToolbarMenu() {
        setSupportActionBar(binding.tbTaskDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            binding.tbTaskDetail.setNavigationOnClickListener {
                this@TaskDetailScreen.onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> goToEditTaskScreen()
            R.id.action_delete -> openBSToDeleteTask(this@TaskDetailScreen)
        }
        return true
    }

    private fun deleteTask() {
        taskDetailViewModel.getTaskModel()?.let {
            taskDetailViewModel.deleteTask(it)
        }
    }

    private fun updateTask(task: TasksModel) {
        taskDetailViewModel.editTask(task)
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
            val task = taskDetailViewModel.getTaskModel()
            task?.let {
                it.status = getString(R.string.bs_status_type_todo)
                updateTask(task)
            }
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
            val task = taskDetailViewModel.getTaskModel()
            task?.let {
                it.status = getString(R.string.bs_status_type_in_progress)
                updateTask(task)
            }
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
            val task = taskDetailViewModel.getTaskModel()
            task?.let {
                it.status = getString(R.string.bs_status_type_done)
                updateTask(task)
            }
            typePickerDialog.dismiss()
        }

        typePickerDialog.show()
    }

    fun openBSToDeleteTask(
        context: Context
    ) {
        val confirmationPickerDialog = BaseBottomSheetDialog(context)
        confirmationPickerDialog.setContentView(R.layout.bs_confirmation)
        val tvTitle = confirmationPickerDialog.findViewById<TextView>(R.id.tvTitle)!!
        val tvNegative =
            confirmationPickerDialog.findViewById<MaterialButton>(R.id.tvNegative)!!
        val tvPositive =
            confirmationPickerDialog.findViewById<MaterialButton>(R.id.tvPositive)!!

        tvTitle.text = context.getString(R.string.confirmation_task_delete_title)

        tvPositive.text = context.getString(R.string.confirmation_task_delete_positive)
        tvNegative.text = context.getString(R.string.confirmation_task_delete_negative)

        tvNegative.setOnClickListener {
            confirmationPickerDialog.dismiss()
        }
        tvPositive.setOnClickListener {
            deleteTask()
            confirmationPickerDialog.dismiss()
        }
        confirmationPickerDialog.show()
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
            is TaskListSingleViewEvent.TasksFetchedSuccessfully -> {

            }

            TaskListSingleViewEvent.TaskDeleteSuccessfully -> {
                showToast(
                    getString(R.string.delete_task_success),
                    Snackbar.LENGTH_LONG
                )
                this@TaskDetailScreen.onBackPressedDispatcher.onBackPressed()
            }

            TaskListSingleViewEvent.TaskEditedSuccessfully -> {
                showToast(
                    getString(R.string.edit_task_success),
                    Snackbar.LENGTH_LONG
                )
            }

            is TaskListSingleViewEvent.TaskFetchedSuccessfully -> {
                updateTaskDetailData(event.task)
            }
        }
    }

    private val EditTaskResponseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    if (it.component?.packageName == applicationContext.packageName) {
                        if (it.getBooleanExtra("Edited", false)) {
                            taskDetailViewModel.getTaskModel()?.let {
                                taskDetailViewModel.fetchTask(it)
                            }
                        }
                    }
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // TODO Email cancel
            }
        }

    private fun goToEditTaskScreen() {
        EditTaskScreen.createIntent(applicationContext, taskDetailViewModel.getTaskModel()).apply {
            EditTaskResponseLauncher.launch(this)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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