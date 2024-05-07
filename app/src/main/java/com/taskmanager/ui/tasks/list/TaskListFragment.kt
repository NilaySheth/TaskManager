package com.taskmanager.ui.tasks.list

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskmanager.R
import com.taskmanager.base.BaseBottomSheetDialog
import com.taskmanager.base.BaseFragment
import com.taskmanager.base.customViews.GenericAdapter
import com.taskmanager.base.customViews.ItemOffsetDecoration
import com.taskmanager.base.mvi.MviView
import com.taskmanager.base.utils.hide
import com.taskmanager.base.utils.show
import com.taskmanager.databinding.FragmentTaskListBinding
import com.taskmanager.databinding.ItemTaskListBinding
import com.taskmanager.ui.tasks.list.detail.TaskDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : BaseFragment(),
    MviView<TaskListViewState, TaskListSingleViewEvent> {

    private lateinit var binding: FragmentTaskListBinding
    private val taskListViewModel by viewModels<TaskListViewModel>()
    private val tasksList: ArrayList<TasksModel> = arrayListOf()

    companion object {
        fun getInstance(): Fragment {
            val fragment = TaskListFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        taskListViewModel.bindMviView(viewLifecycleOwner, this)
        setupUI()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        taskListViewModel.fetchTaskList()
    }

    private fun setupUI() {
        binding.fabFilterBy.setOnClickListener { view ->
            openBSToFilterTasks(requireActivity())
        }
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
                if (binding.rvTaskList.adapter == null) {
                    tasksList.clear()
                    tasksList.addAll(event.taskList)
                    setupAdapter(
                        tasksList,
                    )
                } else {
                    (binding.rvTaskList.adapter as GenericAdapter<TasksModel, ItemTaskListBinding>).updateList(
                        event.taskList,
                    )
                }
            }

            TaskListSingleViewEvent.TaskDeleteSuccessfully -> {
                //Not used in this screen
            }

            TaskListSingleViewEvent.TaskEditedSuccessfully -> {

            }

            is TaskListSingleViewEvent.TaskFetchedSuccessfully -> {

            }
        }
    }

    private fun setupAdapter(taskList: List<TasksModel>) {
        if (taskList.isEmpty()) {
            binding.rvTaskList.hide()
            binding.emptyRVTaskList.show()
        } else {
            binding.rvTaskList.show()
            binding.emptyRVTaskList.hide()
            binding.rvTaskList.layoutManager =
                LinearLayoutManager(
                    binding.root.context,
                    LinearLayoutManager.VERTICAL,
                    false,
                )
            binding.rvTaskList.addItemDecoration(
                ItemOffsetDecoration(
                    binding.root.context,
                    R.dimen.dp_4,
                ),
            )
            binding.rvTaskList.adapter =
                GenericAdapter(
                    taskList,
                    object :
                        GenericAdapter.ItemBinder<TasksModel, ItemTaskListBinding> {
                        override fun createBinding(
                            inflater: LayoutInflater,
                            parent: ViewGroup?,
                        ): ItemTaskListBinding {
                            return ItemTaskListBinding.inflate(
                                inflater,
                                parent,
                                false,
                            )
                        }

                        override fun bind(
                            binding: ItemTaskListBinding,
                            data: TasksModel,
                            callback: ((TasksModel, Int) -> Unit)?,
                            position: Int,
                        ) {
                            binding.tvTitle.text = data.title
                            binding.tvDesc.text = data.desc
                            when (data.status) {
                                getString(R.string.bs_status_type_todo) -> {
                                    binding.vStatus.setBackgroundColor(
                                        ContextCompat.getColor(
                                            binding.root.context,
                                            R.color.statusTodo
                                        )
                                    )
                                }

                                getString(R.string.bs_status_type_in_progress) -> {
                                    binding.vStatus.setBackgroundColor(
                                        ContextCompat.getColor(
                                            binding.root.context,
                                            R.color.statusInProgress
                                        )
                                    )
                                }

                                getString(R.string.bs_status_type_done) -> {
                                    binding.vStatus.setBackgroundColor(
                                        ContextCompat.getColor(
                                            binding.root.context,
                                            R.color.statusDone
                                        )
                                    )
                                }
                            }
                            binding.root.setOnClickListener {
                                callback?.invoke(data, position)
                            }
                        }
                    },
                    callback = { task, pos ->
                        goToListDetailScreen(task)
                    },
                )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun openBSToFilterTasks(context: Context) {
        val typePickerDialog = BaseBottomSheetDialog(context)
        typePickerDialog.setContentView(R.layout.bs_filter_task)
        val tvAll: TextView = typePickerDialog.findViewById(R.id.tvAll)!!
        val tvTodo: TextView = typePickerDialog.findViewById(R.id.tvTodo)!!
        val tvInProgress: TextView = typePickerDialog.findViewById(R.id.tvInProgress)!!
        val tvDone: TextView = typePickerDialog.findViewById(R.id.tvDone)!!

        tvAll.setOnClickListener {
            taskListViewModel.getFilterByTasksStatus(
                tasksList,
                getString(R.string.bs_status_type_all)
            )
            typePickerDialog.dismiss()
        }
        tvTodo.setOnClickListener {
            taskListViewModel.getFilterByTasksStatus(
                tasksList,
                getString(R.string.bs_status_type_todo)
            )
            typePickerDialog.dismiss()
        }
        tvInProgress.setOnClickListener {
            taskListViewModel.getFilterByTasksStatus(
                tasksList,
                getString(R.string.bs_status_type_in_progress)
            )
            typePickerDialog.dismiss()
        }
        tvDone.setOnClickListener {
            taskListViewModel.getFilterByTasksStatus(
                tasksList,
                getString(R.string.bs_status_type_done)
            )
            typePickerDialog.dismiss()
        }

        typePickerDialog.show()
    }


    private fun goToListDetailScreen(tasksModel: TasksModel) {
        TaskDetailScreen.createIntent(requireContext(), tasksModel).apply {
            startActivity(this)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }
    }
}