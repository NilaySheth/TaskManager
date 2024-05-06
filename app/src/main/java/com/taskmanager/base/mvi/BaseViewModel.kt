package com.taskmanager.base.mvi

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
@Deprecated("Use MviViewModel instead")
abstract class BaseViewModel<VS: ViewState,VSE: ViewSideEffect>:ViewModel(),ContainerHost<VS,VSE>