package com.taskmanager.ui.tasks.list

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class TasksModel(
    @get: PropertyName("title") @set: PropertyName("title") var title: String = "",
    @get: PropertyName("desc") @set: PropertyName("desc") var desc: String = "",
    @get: PropertyName("status") @set: PropertyName("status") var status: String = "",
    @get: PropertyName("date") @set: PropertyName("date") var date: String = "",
) : Parcelable
