package com.taskmanager.ui.tasks.list.detail

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
import com.taskmanager.R
import com.taskmanager.base.BaseActivity
import com.taskmanager.base.BaseBottomSheetDialog
import com.taskmanager.base.mvi.MviView
import com.taskmanager.base.utils.hide
import com.taskmanager.base.utils.show
import com.taskmanager.databinding.ActivityTaskDetailBinding
import com.taskmanager.ui.tasks.create.CreateTaskScreen
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
        setDataFromIntent()
        binding.mcvStatus.setOnClickListener {
            openBSToSelectStatus(this@TaskDetailScreen)
        }
    }

    private fun setDataFromIntent() {
        val task = intent.getParcelableExtra<TasksModel>("taskModel")
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
            R.id.action_delete -> deleteTask()
        }
        return true
    }

    private fun deleteTask() {

    }

    private fun updateTask(task: TasksModel) {

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
        }
    }

    private fun goToEditTaskScreen() {
        CreateTaskScreen.createIntent(applicationContext).apply {
            startActivity(this)
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