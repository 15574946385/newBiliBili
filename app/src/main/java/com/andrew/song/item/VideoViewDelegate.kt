package com.andrew.song.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrew.song.base.list.base.BaseItemViewDelegate
import com.andrew.song.databinding.ItemVideoBinding

class VideoViewDelegate : BaseItemViewDelegate<VideoViewData, VideoViewDelegate.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemVideoBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VideoViewData) {
        super.onBindViewHolder(holder, item)
    }

    class ViewHolder(val viewBinding: ItemVideoBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    }
}