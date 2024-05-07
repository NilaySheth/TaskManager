package com.taskmanager.ui.tasks.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.taskmanager.R
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

    private fun setupUI() {
        taskListViewModel.fetchTaskList()
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
                    setupAdapter(
                        event.taskList,
                    )
                } else {
                    (binding.rvTaskList.adapter as GenericAdapter<TasksModel, ItemTaskListBinding>).updateList(
                        event.taskList,
                    )
                }
                event.taskList
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