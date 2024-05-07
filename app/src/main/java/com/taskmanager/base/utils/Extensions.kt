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

fun TextInputEditText.limitDecimalPlaces(maxDecimalPlaces: Int) {
    filters += InputFilter { source, _, _, dest, dstart, dend ->
        val value = if (source.isEmpty()) {
            dest.removeRange(dstart, dend)
        } else {
            StringBuilder(dest).insert(dstart, source)
        }
        val matcher =
            Pattern.compile("([1-9][0-9]*)|([1-9][0-9]*\\.[0-9]{0,$maxDecimalPlaces})|(\\.[0-9]{0,$maxDecimalPlaces})")
                .matcher(value)
        if (!matcher.matches()) "" else null
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun Activity.hideKeyboard() {
    try {
        val inputManager =
            this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (this.currentFocus != null) {
            inputManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken, 0
            )
        } else {
            inputManager.hideSoftInputFromWindow(
                View(this).windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextInputEditText.getTextFromTiet(): String {
    return this.text.toString().trim()
}