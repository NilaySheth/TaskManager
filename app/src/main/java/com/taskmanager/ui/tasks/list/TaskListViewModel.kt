package com.taskmanager.ui.tasks.list

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
                                val task = result.toObject(TasksModel::class.java)
                                task.id = result.id
                                tasksList.add(task)
                            }
                            tasksList.sortBy {
                                it.date
                            }
                        } else {
                            Log.d("Fetch Task List", "No such document")
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
        Log.d("Fetch Tasks time", "Total time taken: $time ms")
    }

    fun fetchTask(task: TasksModel) {
        val time = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {
                updateViewState(stateReducer = {
                    it.copy(loading = true)
                })

                val db = Firebase.firestore
                val userTaskDocumentRef =
                    db.collection("tasks").document("users").collection(UserPreferences.userId)
                        .document(task.id)
                userTaskDocumentRef.get()
                    .addOnSuccessListener { result ->
                        result.data.apply {
                            task.title = this?.get("title").toString()
                            task.desc = this?.get("desc").toString()
                            task.status = this?.get("status").toString()
                        }
                        task.id = result.id
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
                        TaskListSingleViewEvent.TaskFetchedSuccessfully(
                            task
                        )
                    )
                }

            }
        }
        Log.d("Fetch Task time", "Total time taken: $time ms")
    }

    fun editTask(tasksModel: TasksModel) {
        val time = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {
                updateViewState(stateReducer = {
                    it.copy(loading = true)
                })

                val db = Firebase.firestore
                val userTaskDocumentRef =
                    db.collection("tasks").document("users").collection(UserPreferences.userId)
                        .document(tasksModel.id)
                userTaskDocumentRef.update(
                    mapOf(
                        "title" to tasksModel.title,
                        "desc" to tasksModel.desc,
                        "status" to tasksModel.status,
                        "date" to Timestamp(Date()),
                    ),
                )
                    .addOnSuccessListener {

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
                        TaskListSingleViewEvent.TaskEditedSuccessfully
                    )
                }
            }
        }
        Log.d("Edit Task time", "Total time taken: $time ms")
    }

    fun deleteTask(tasksModel: TasksModel) {
        val time = measureTimeMillis {
            viewModelScope.launch(Dispatchers.IO) {
                updateViewState(stateReducer = {
                    it.copy(loading = true)
                })

                val db = Firebase.firestore
                val userTaskDocumentRef =
                    db.collection("tasks").document("users").collection(UserPreferences.userId)
                        .document(tasksModel.id)
                userTaskDocumentRef.delete()
                    .addOnSuccessListener {

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
                        TaskListSingleViewEvent.TaskDeleteSuccessfully
                    )
                }

            }
        }
        Log.d("Delete Task time", "Total time taken: $time ms")
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

    fun getFilterByTasksStatus(
        dataList: ArrayList<TasksModel>,
        selectedFilterType: String
    ): ArrayList<TasksModel> {

        return if (selectedFilterType == "All") {
            dataList
        } else {
            val filteredList = arrayListOf<TasksModel>()
            dataList.forEach { data ->
                if (data.status == selectedFilterType) {
                    filteredList.add(data)
                }
            }
            filteredList
        }
    }
}