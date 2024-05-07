package com.taskmanager.base.customViews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class GenericAdapter<T : Any, VB : ViewBinding>(
    itemList: List<T>,
    private val itemBinder: ItemBinder<T, VB>,
    private val callback: ((T, Int)->Unit)? = null
) : RecyclerView.Adapter<GenericAdapter.ViewHolder<VB>>() {
    private val diffUtil = object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    init {
        updateList(itemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<VB> {
        val binding = itemBinder.createBinding(LayoutInflater.from(parent.context), parent)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder<VB>, position: Int) {
        val item = asyncListDiffer.currentList[position]
        itemBinder.bind(holder.binding, item, callback, position)
    }

    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    fun updateList(list: List<T>){
        asyncListDiffer.submitList(list)
    }

    class ViewHolder<VB: ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root){

    }

    interface ItemBinder<T, VB : ViewBinding?> {
        fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): VB
        fun bind(binding: VB, item: T, callback: ((T, Int)->Unit)?, position: Int)
    }
}