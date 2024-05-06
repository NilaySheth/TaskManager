package com.taskmanager.base.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.SettingsBuilder
import org.orbitmvi.orbit.viewmodel.container

abstract class MviViewModel<VS: ViewState,VSE: SingleViewEvent>: ViewModel(),
    MviComponent<VS, VSE> {
    abstract fun initialState():VS
    override val container: Container<VS, VSE> by lazy {
        container(initialState(), buildContainerSetting())
    }

    private fun buildContainerSetting(): SettingsBuilder.()->Unit{
        return {
            exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                handleMviExceptions(throwable)
            }
        }
    }

    open fun handleMviExceptions(throwable: Throwable){}
}