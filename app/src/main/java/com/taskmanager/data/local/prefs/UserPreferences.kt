package com.taskmanager.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private lateinit var mPrefs: SharedPreferences
    private lateinit var mPrefsEditor: SharedPreferences.Editor

    fun init(context: Context) {
        mPrefs = context.getSharedPreferences(APConstants.USER_SHARED_PREFERENCES, 0)
        mPrefsEditor = mPrefs.edit()
    }

    var emailAddress: String?
        get() = mPrefs.getString(APConstants.USER_EMAIL, "")!!
        set(value) {
            mPrefsEditor.apply {
                putString(APConstants.USER_EMAIL, value)
                commit()
            }
        }

    var userId: String
        get() = mPrefs.getString(APConstants.USER_ACCESS_USER_ID, "")!!
        set(value) {
            mPrefsEditor.apply {
                putString(APConstants.USER_ACCESS_USER_ID, value)
                commit()
            }
        }

    var fullName: String
        get() = mPrefs.getString(
            APConstants.USER_FULLNAME, "")!!
        set(value) {
            mPrefsEditor.apply {
                putString(APConstants.USER_FULLNAME, value)
                commit()
            }
        }

    fun userLogout() {
        mPrefsEditor.clear().apply()
    }
}