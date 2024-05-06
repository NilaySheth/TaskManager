package com.taskmanager.base.utils

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kotlin.reflect.KClass

object FragmentUtils {
    fun addFragmentToContainer(activity: FragmentActivity, fragment: Fragment, view: ViewGroup, addToBackStack:Boolean = false){
        val fragMan: FragmentManager = activity.supportFragmentManager
        val fragTransaction: FragmentTransaction = fragMan.beginTransaction()
        fragTransaction.add(view.id, fragment, fragment::class.java.simpleName)
        if(addToBackStack)
            fragTransaction.addToBackStack(fragment::class.java.simpleName)
        fragTransaction.commit()
    }
    fun addFragmentToContainer(activity: FragmentActivity, fragment: Fragment, layoutID: Int, addToBackStack:Boolean = false){
        val fragMan: FragmentManager = activity.supportFragmentManager
        val fragTransaction: FragmentTransaction = fragMan.beginTransaction()
        fragTransaction.add(layoutID, fragment, fragment::class.java.simpleName)
        if(addToBackStack)
            fragTransaction.addToBackStack(fragment::class.java.simpleName)
        fragTransaction.commit()
    }

    fun addFragmentToContainer(fragMan: FragmentManager, fragment: Fragment, view: ViewGroup, addToBackStack:Boolean = false){
        val fragTransaction: FragmentTransaction = fragMan.beginTransaction()
        fragTransaction.add(view.id, fragment, fragment::class.java.simpleName)
        if(addToBackStack)
            fragTransaction.addToBackStack(fragment::class.java.simpleName)
        fragTransaction.commit()
    }
    fun addFragmentToContainer(fragMan: FragmentManager, fragment: Fragment, layoutID: Int, addToBackStack:Boolean = false){
        val fragTransaction: FragmentTransaction = fragMan.beginTransaction()
        fragTransaction.add(layoutID, fragment, fragment::class.java.simpleName)
        if(addToBackStack)
            fragTransaction.addToBackStack(fragment::class.java.simpleName)
        fragTransaction.commit()
    }

    fun removeFragment(activity: FragmentActivity, fragmentClass: KClass<out Fragment>){
        val fragMan: FragmentManager = activity.supportFragmentManager
        val fragTransaction: FragmentTransaction = fragMan.beginTransaction()
        val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentClass.java.simpleName)
        fragment?.let {
            fragTransaction.remove(it)
        }
        fragTransaction.commit()
    }
}