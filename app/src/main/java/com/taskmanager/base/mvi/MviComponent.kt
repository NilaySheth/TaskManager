package com.taskmanager.base.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitExperimental
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.observe
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface MviComponent<VS: ViewState,VSE: SingleViewEvent>: ContainerHost<VS, VSE> {
    fun getCurrentState():VS{
        return container.stateFlow.value
    }

    fun bindMviView(
        lifecycleOwner: LifecycleOwner,
        mviView: MviView<VS, VSE>,
        lifecycleState: Lifecycle.State = Lifecycle.State.CREATED) {
        observe(lifecycleOwner, lifecycleState,mviView::handleViewState, mviView::handleSingleViewEvent)
    }

    suspend fun postSingleViewEvent(event:VSE){
        suspendCoroutine<Unit> { continuation ->
            intent {
                postSideEffect(event)
                continuation.resume(Unit)
            }
        }
    }

    suspend fun updateViewState(
        stateReducer: (VS) -> VS
    ) {
        suspendCoroutine<Unit> { continuation ->
            intent {
                reduce { stateReducer.invoke(state) }
                continuation.resume(Unit)
            }
        }
    }

    @OptIn(OrbitExperimental::class)
    suspend fun modifyViewState(stateReducer: (VS)->VS){
        suspendCoroutine<Unit> { continuation ->
            blockingIntent {
                reduce { stateReducer.invoke(state) }
                continuation.resume(Unit)
            }
        }
    }
}