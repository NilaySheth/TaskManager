package com.taskmanager.base.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.taskmanager.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern


fun Context.showToast(message: String?, toastLength: Int = Toast.LENGTH_SHORT) {
    message?.let {
        if (message.isNotEmpty()) {
            Toast.makeText(this, message, toastLength).show()
        }
    }
}

fun View.snack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    actionColor: Int = Color.YELLOW,
    anchorView: View = this
) {
    Snackbar.make(this, message, length)
        .setAnchorView(anchorView)
        .setActionTextColor(actionColor)
        .show()
}

fun View.snack(
    message: String,
    actionText: String,
    actionColor: Int = Color.YELLOW,
    length: Int = Snackbar.LENGTH_LONG,
    listener: (View) -> Unit
) {
    Snackbar.make(this, message, length)
        .setAction(actionText, listener)
        .setActionTextColor(actionColor)
        .show()
}

fun View.noInternetSnack(context: Context, listener: (View) -> Unit) {
    snack(
        context.getString(R.string.validation_no_internet),
        context.getString(R.string.retry),
        Color.YELLOW,
        Snackbar.LENGTH_INDEFINITE,
        listener
    )
}

fun timestampToDateFormat(timeStamp: Long, pattern: String): String {
    val date = Date(timeStamp * 1000)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(date)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun TextInputEditText.getTextFromTiet(): String {
    return this.text.toString().trim()
}