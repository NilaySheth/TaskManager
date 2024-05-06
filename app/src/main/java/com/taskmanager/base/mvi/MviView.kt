package com.taskmanager.base.mvi

interface MviView<VS: ViewState,VSE: SingleViewEvent> {
    fun handleViewState(state:VS)
    fun handleSingleViewEvent(event: VSE)
}