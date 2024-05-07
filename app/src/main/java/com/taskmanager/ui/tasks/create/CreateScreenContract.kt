package com.taskmanager.ui.tasks.create

import com.taskmanager.base.mvi.SingleViewEvent
import com.taskmanager.base.mvi.ViewState

data class CreateTaskViewState(
    val loading: Boolean = false,
) : ViewState

sealed interface CreateTaskSingleViewEvent : SingleViewEvent {
    data class TaskCreatedSuccessfully(val taskId: String) : CreateTaskSingleViewEvent
}