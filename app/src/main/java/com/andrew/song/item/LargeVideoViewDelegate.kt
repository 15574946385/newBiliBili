package com.andrew.song.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.song.base.list.base.BaseItemViewDelegate
import com.andrew.song.databinding.ItemLargeVideoBinding

class LargeVideoViewDelegate : BaseItemViewDelegate<LargeVideoViewData, LargeVideoViewDelegate.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemLargeVideoBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LargeVideoViewData) {
        super.onBindViewHolder(holder, item)
    }

    class ViewHolder(val viewBinding: ItemLargeVideoBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    }
}