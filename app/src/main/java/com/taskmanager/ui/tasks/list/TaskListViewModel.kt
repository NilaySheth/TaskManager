package com.taskmanager.ui.tasks.list

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.taskmanager.base.mvi.MviViewModel
import com.taskmanager.data.local.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.system.measureTimeMillis

@HiltViewModel
class TaskListViewModel
@Inject
constructor(
    @ApplicationContext val context: Context,
) : MviViewModel<TaskListViewState, TaskListSingleViewEvent>() {
    override fun initialState(): TaskListViewState {
        return TaskListViewState(
            loading = false,
        )
    }

    fun fetchTaskList() {
        val time = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {
                updateViewState(stateReducer = {
                    it.copy(loading = true)
                })

                val tasksList: ArrayList<TasksModel> = arrayListOf()
                val db = Firebase.firestore
                val userTaskCollectionRef =
                    db.collection("tasks").document("users").collection(UserPreferences.userId)
                userTaskCollectionRef.get()
                    .addOnSuccessListener { results ->
                        if (results != null) {
                            results.forEach { result ->
                                tasksList.add(result.toObject(TasksModel::class.java))
                            }
                            Log.d("TAG", "DocumentSnapshot data: ${results.documents}")
                        } else {
                            Log.d("TAG", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
                    .await()

                updateViewState(stateReducer = {
                    it.copy(
                        loading = false,
                    )
                })
                withContext(Dispatchers.Main) {
                    postSingleViewEvent(
                        TaskListSingleViewEvent.TasksFetchedSuccessfully(
                            tasksList
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

    private var taskModel: TasksModel? = null
    fun setTaskModel(matchListResponseModel: TasksModel) {
        this.taskModel = matchListResponseModel
    }

    fun getTaskModel(): TasksModel? {
        return taskModel
    }
}