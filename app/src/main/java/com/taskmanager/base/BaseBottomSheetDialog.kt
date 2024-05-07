package com.taskmanager.base

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.taskmanager.R

class BaseBottomSheetDialog(
    context: Context,
    style: Int = R.style.BottomSheetDialog
) :
    BottomSheetDialog(context, style) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}