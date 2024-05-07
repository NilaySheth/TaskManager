package com.taskmanager.ui.tasks.create

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.taskmanager.base.mvi.MviViewModel
import com.taskmanager.data.local.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

@HiltViewModel
class CreateTaskViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
) : MviViewModel<CreateTaskViewState, CreateTaskSingleViewEvent>() {
    override fun initialState(): CreateTaskViewState {
        return CreateTaskViewState(
            loading = false,
        )
    }

    fun createTask(title: String, desc: String, state: String) {
        val time = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {
                updateViewState(stateReducer = {
                    it.copy(loading = true)
                })

                val db = Firebase.firestore
                val taskInfo = hashMapOf(
                    "title" to title,
                    "desc" to desc,
                    "status" to state,
                    "date" to Timestamp(Date()),
                )

                val task =
                    db.collection("tasks").document("users").collection(UserPreferences.userId)
                        .add(taskInfo)
                        .addOnSuccessListener { documentReference ->
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                        .await()

                updateViewState(stateReducer = {
                    it.copy(
                        loading = false,
                    )
                })
                withContext(Dispatchers.Main) {
                    postSingleViewEvent(
                        CreateTaskSingleViewEvent.TaskCreatedSuccessfully(
                            task.id
                        )
                    )
                }

            }
        }
        Log.d("Create Task time", "Total time taken: $time ms")
    }

    suspend fun <T> Task<T>.await(): T {
        return suspendCoroutine { cont ->
            addOnCompleteListener {
                if (it.exception != null) {
                    cont.resumeWithException(it.exception!!)
                } else {
                }
            }
            addOnSuccessListener { cont.resume(it) }
            addOnFailureListener { cont.resumeWithException(it) }
        }
    }
}