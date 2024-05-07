package com.taskmanager.ui.tasks.list

import com.taskmanager.base.mvi.SingleViewEvent
import com.taskmanager.base.mvi.ViewState

data class TaskListViewState(
    val loading: Boolean = false,
) : ViewState

sealed interface TaskListSingleViewEvent : SingleViewEvent {
    data class TasksFetchedSuccessfully(val taskList: ArrayList<TasksModel>) : TaskListSingleViewEvent
}